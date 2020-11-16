package com.github.osmplayer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ProgressBar
import androidx.annotation.RequiresApi


class VolBar : ProgressBar {
    private var audioManager: AudioManager? = null
    var mAX_VOLUME = 0
        private set
    private val MIN_VOLUME = 0
    private val volumeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateVolumeProgress()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context?) : super(context) {
        initialise()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialise()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialise()
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = VolBar::class.java.name
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = VolBar::class.java.name
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerVolumeReceiver()
    }

    override fun onDetachedFromWindow() {
        unregisterVolumeReceiver()
        super.onDetachedFromWindow()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initialise() {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        this.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        this.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        mAX_VOLUME = max
    }

    private fun updateVolumeProgress() {
        this.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    private fun registerVolumeReceiver() {
        context.registerReceiver(
            volumeReceiver,
            IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        )
    }

    private fun unregisterVolumeReceiver() {
        context.unregisterReceiver(volumeReceiver)
    }

    @Synchronized
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        try {
            audioManager!!.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                progress,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
        } catch (e: Exception) {
            Log.e("error", "once")
        }
    }
}