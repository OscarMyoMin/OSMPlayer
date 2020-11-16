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
        osmPlayer!!.setLogo(R.drawable.ic_play)
        OSMPlayer.SRTSTYLE_DEFAULT = false
        osmPlayer!!.srtStyle(
                Color.WHITE,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.RED,
                10
        )
        //osmPlayer!!.play(this,  "https://eboxmovie.sgp1.digitaloceanspaces.com/song/aapyokyi.mp4",)
        osmPlayer!!.play(this,  "https://d3cs5y5559s57s.cloudfront.net/live/channelk.m3u8?wmsAuthSign=c2VydmVyX3RpbWU9MTAvMTgvMjAyMCA5OjMyOjA3IEFNJmhhc2hfdmFsdWU9TUh2ajBCc2c4L1dXTGE3empUb3dlZz09JnZhbGlkbWludXRlcz0yNSZzdHJtX2xlbj00","HLS")
    }
}