package com.github.osmplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.github.osmplayer.utils.BrightnessUtils
import com.github.osmplayer.utils.VolBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import org.jetbrains.annotations.NonNls
import java.util.*

class OSMPlayer : AppCompatActivity(){

    companion object{
        private const val VIDEO_URL = "VIDEO_URL"
        private const val SUBTITLE_URL = "SUBTITLE_URL"
        private const val VIDEO_TYPE = "VIDEO_TYPE"
        var SHOW_LOGO = false
        private var LOGO = 0
        var SRTSTYLE_DEFAULT = false
        private var FONT_SIZE = 0
        private var FCOLOR = 0
        private var BCOLOR = 0
        private var WCOLOR = 0
        private var SCOLOR = 0
    }

    private var doubleBackToExitPressedOnce = false
    var exoPlayerView: PlayerView? = null
    var exoPlayer: SimpleExoPlayer? = null
    var progressLoading: ProgressBar? = null
    var btnSrt: TextView? = null
    var btnQty:TextView? = null
    var btnLock: ImageView? = null
    var btnUnlock:ImageView? = null
    var exoLogo : ImageView?=null
    var timer : Timer?=null
    var isLock = false
    var duration: Long = 0


    fun setLogo(image: Int){
        LOGO = image
    }
    fun play(@NonNull context: Context, @NonNull videoURL: String, @NonNull videoType: String, @Nullable srt : String) {
        val intent = Intent(context, OSMPlayer::class.java)
        intent.putExtra(VIDEO_URL, videoURL)
        intent.putExtra(SUBTITLE_URL, srt)
        intent.putExtra(VIDEO_TYPE, videoType)
        context.startActivity(intent)
    }

    fun play(@NonNull context: Context, @NonNull videoURL: String, @NonNull videoType: String){
        val intent = Intent(context, OSMPlayer::class.java)
        intent.putExtra(VIDEO_URL, videoURL)
        intent.putExtra(SUBTITLE_URL, "")
        intent.putExtra(VIDEO_TYPE, videoType)
        context.startActivity(intent)
    }

    fun srtStyle(
            foregroundColor: Int,
            backgroundColor: Int,
            windowColor: Int,
            strokeColor: Int,
            fontSize: Int
    ) {
        FCOLOR = foregroundColor
        BCOLOR = backgroundColor
        WCOLOR = windowColor
        SCOLOR = strokeColor
        FONT_SIZE = fontSize
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.osm_player)

        timer = Timer()

        val videoURL = intent.getStringExtra(VIDEO_URL)
        val subtitleURL = intent.getStringExtra(SUBTITLE_URL)
        var videoType = intent.getStringExtra(VIDEO_TYPE)

        exoPlayerView = findViewById(R.id.playerView)
        progressLoading = findViewById(R.id.progresbar_video_play)
        exoLogo = findViewById(R.id.exo_logo)
        btnLock = findViewById(R.id.exo_lock)
        btnUnlock = findViewById(R.id.exo_unlock)
        btnSrt = findViewById(R.id.exo_subtitle)
        btnQty = findViewById(R.id.exo_quality)



        exoPlayer = createPlayer()
        exoPlayerView!!.player = exoPlayer

        if(SHOW_LOGO){
            exoLogo!!.visibility = View.VISIBLE
            if (LOGO == null){
                exoLogo!!.setImageResource(R.drawable.ic_logo)
            }else{
                exoLogo!!.setImageResource(LOGO)
            }
        }else{
            exoLogo!!.visibility = View.GONE
        }
        if (SRTSTYLE_DEFAULT) {
            setSRTStyle(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, 10)
        } else {
            setSRTStyle(FCOLOR, BCOLOR, WCOLOR, SCOLOR, FONT_SIZE)
        }

        if (subtitleURL.isEmpty()) {
            if (!videoType.isEmpty()) {
                videoType = videoType.toUpperCase()
                if (videoType == "HLS") {
                    exoPlayer!!.prepare(prepareDataSource(videoURL, true))
                    exoPlayer!!.playWhenReady = true
                }
            } else {
                exoPlayer!!.prepare(prepareDataSource(videoURL, false))
                exoPlayer!!.playWhenReady = true
            }
        } else {
            exoPlayer!!.prepare(prepareDataSource(videoURL, subtitleURL))
            exoPlayer!!.playWhenReady = true
        }

        if (videoType.isEmpty()) {
            timer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    duration = exoPlayer!!.currentPosition
                }
            }, 0, 1000)
        }

        // Controller
        btnLock!!.setOnClickListener(View.OnClickListener {
            isLock = true
            if (isLock) {
                hideController()
            } else {
                showController()
            }
        })

        btnUnlock!!.setOnClickListener(View.OnClickListener {
            isLock = false
            if (isLock) {
                hideController()
            } else {
                showController()
            }
        })

        btnSrt!!.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                    applicationContext,
                    "Support Later",
                    Toast.LENGTH_SHORT
            ).show()
        })

        btnQty!!.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                    applicationContext,
                    "Support Later",
                    Toast.LENGTH_SHORT
            ).show()
        })

        progressLoading!!.visibility = View.VISIBLE
        var returnResultOnce :Boolean = true
        exoPlayer!!.addListener(object : Player.EventListener{
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if(playbackState == Player.STATE_READY && returnResultOnce){
                    setResult(Activity.RESULT_OK)
                    progressLoading!!.visibility = View.GONE
                    returnResultOnce = false
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                setResult(Activity.RESULT_CANCELED)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    finishAndRemoveTask()
                }
            }

            override fun onPositionDiscontinuity(reason: Int) {

            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

            }

            override fun onSeekProcessed() {

            }

        })
        exoPlayer!!.playWhenReady = true

    }

    private fun createPlayer(): SimpleExoPlayer? {
        // 1. Create a default TrackSelector
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(
                bandwidthMeter
        )
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        // 2. Create the player
        return ExoPlayerFactory.newSimpleInstance(this, trackSelector)
    }

    private fun prepareDataSource(videoURL: String, subtitleURL: String): MergingMediaSource {
        // Measures bandwidth during playback. Can be null if not required.
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, "STREAMER"),
                defaultBandwidthMeter
        )
        // Produces Extractor instances for parsing the media data.
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource = ExtractorMediaSource(
                Uri.parse(videoURL),
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        )
        // Build the subtitle MediaSource.
        @SuppressLint("WrongConstant")
        val subtitleFormat = Format.createTextSampleFormat(
                null,  // An identifier for the track. May be null.
                MimeTypes.APPLICATION_SUBRIP,  // The mime type. Must be set correctly.
                C.TRACK_TYPE_TEXT,  // Selection flags for the track.
                "Arabic"
        ) // The subtitle language. May be null.
        val subtitleSource: MediaSource = SingleSampleMediaSource(
                Uri.parse(subtitleURL),
                dataSourceFactory,
                subtitleFormat,
                C.TIME_UNSET
        )
        // Plays the video with the side loaded subtitle.
        return MergingMediaSource(videoSource, subtitleSource)
    }

    private fun prepareDataSource(videoURL: String, videoType: Boolean) : MediaSource{
        var defaultBandwidthMeter : DefaultBandwidthMeter = DefaultBandwidthMeter()
        var dataSourceFactory : DataSource.Factory = DefaultDataSourceFactory(this, Util.getUserAgent(this, applicationInfo.loadLabel(packageManager).toString()), defaultBandwidthMeter)
        var extractorsFactory : ExtractorsFactory = DefaultExtractorsFactory()
        if(videoType){
            return HlsMediaSource(Uri.parse(videoURL), dataSourceFactory, null, null)
        }else{
            return ExtractorMediaSource(Uri.parse(videoURL), dataSourceFactory, extractorsFactory, null, null)
        }
    }

    fun setSRTStyle(foregroundColor: Int, backgroundColor: Int, windowColor: Int, strokeColor: Int, fontSize: Int){
        exoPlayerView!!.subtitleView.setApplyEmbeddedStyles(false)
        exoPlayerView!!.subtitleView.setApplyEmbeddedFontSizes(false)
        exoPlayerView!!.subtitleView.setStyle(
                CaptionStyleCompat(
                        foregroundColor,
                        backgroundColor,
                        windowColor,
                        CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                        strokeColor,
                        null
                )
        )
        exoPlayerView!!.subtitleView.setFixedTextSize(
                TypedValue.COMPLEX_UNIT_PT,
                fontSize.toFloat()
        )
    }
    private fun showController(){
        exoPlayerView!!.showController()
        exoPlayerView!!.useController = true
        btnUnlock!!.visibility = View.GONE
    }

    private fun hideController(){
        exoPlayerView!!.hideController()
        exoPlayerView!!.useController = false
        btnUnlock!!.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        exoPlayer!!.seekTo(duration)
        exoPlayer!!.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        exoPlayer!!.playWhenReady = false
        duration = exoPlayer!!.currentPosition

        val activityManager: ActivityManager = applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer!!.release()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}