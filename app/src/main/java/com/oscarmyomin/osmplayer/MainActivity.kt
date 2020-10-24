package com.oscarmyomin.osmplayer

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.osmplayer.OSMPlayer

class MainActivity : AppCompatActivity() {

    var osmPlayer : OSMPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        osmPlayer = OSMPlayer()
        OSMPlayer.SHOW_LOGO = false
        OSMPlayer.SRTSTYLE_DEFAULT = false
        osmPlayer!!.srtStyle(
                Color.WHITE,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.RED,
                10
        )
        osmPlayer!!.play(this,  "https://eboxmovie.sgp1.digitaloceanspaces.com/song/aapyokyi.mp4",)
    }
}