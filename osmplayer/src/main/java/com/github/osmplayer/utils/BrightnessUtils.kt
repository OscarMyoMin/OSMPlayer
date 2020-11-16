package com.github.osmplayer.utils

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

object BrightnessUtils {
    const val MAX_BRIGHTNESS = 255
    const val MIN_BRIGHTNESS = 0
    operator fun set(context: Context, brightness: Int) {
        val cResolver: ContentResolver = context.getContentResolver()
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
    }

    operator fun get(context: Context): Int {
        val cResolver: ContentResolver = context.getContentResolver()
        return try {
            Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            0
        }
    }
}