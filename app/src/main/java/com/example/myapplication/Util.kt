package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.utilities.Animationz.fadeInAnim1
import com.example.myapplication.utilities.Animationz.fadeOutAnim1
import com.example.myapplication.utilities.Animationz.slideOutRight

abstract class FragmentInputSettings(
    var fragmentManager: FragmentManager,
    var fragment: Fragment,
    var layoutId: Int,
    @Nullable var tag: String? = "",
    var replace: Boolean? = false,
    var animate: Boolean? = false,
)

fun makeLogD(sText: String) = Log.d("!", sText)

fun AppCompatButton.btnChangeText(text: String) = apply { this@btnChangeText.text = text }

fun Int.isZero() = (this == 0)
fun Int.isEqualTo(value: Int) = (this == value)

//fun Boolean.runUnitTrue(uTrue: ()-> Unit) { if (this) uTrue() }
//fun Boolean.runUnitTrueElse(uTrue: () -> Unit, uElse: () -> Unit) = if (this) { uTrue() } else { uElse() }
//fun MutableList<Unit>.runListUnits() = this.forEach { return@forEach it }
//fun MutableList<*>.fromListSetViewsVis(vis: Int) {for (element in this.filterIsInstance<View>() ) element.visibility = vis }

fun Activity.hideSoftKeyBoard(view: View) {
    (this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        view.windowToken,
        0
    )
}

fun View.setViewVisibilityFadeInOut(visible: Boolean) {
    when (visible) {
        true -> {
            apply {
                if (this.visibility != View.VISIBLE) {
                    visibility = View.VISIBLE
                    fadeInAnim1().start()
                }
            }
        }
        false -> {
            apply {
                if (this.visibility != View.INVISIBLE) {
                    visibility = View.INVISIBLE
                    fadeOutAnim1().start()
                }
            }
        }
    }
}

fun View.viewApplyVis(visibility: Int? = null) {
    when (visibility) {
        8, 4 -> {
            this.visibility = visibility; slideOutRight()
        }
        else -> {
            this.visibility = visibility ?: 0; slideOutRight()
        }
    }
}

fun createMediaPlayer(context: Context, rawRes: Int): MediaPlayer {

    return MediaPlayer.create(context, rawRes).apply {

        setAudioAttributes(
            AudioAttributes.Builder()
                .apply {
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    setUsage(AudioAttributes.USAGE_GAME)
                }.build()
        )

        playbackParams = PlaybackParams().apply {
            audioFallbackMode = PlaybackParams.AUDIO_FALLBACK_MODE_MUTE
            speed = 1.0f
        }

        setOnCompletionListener {
            makeLogD("Sound completed $it + $duration")
            if (isPlaying) {
                pause()
            }
        }

        setOnErrorListener(MediaPlayer.OnErrorListener { mp, _, _ ->
            with(mp) {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
                return@OnErrorListener false
            }
        })

    }
}

fun FragmentInputSettings.newFragmentInstance(): FragmentTransaction {

    val f = this

    return f.fragmentManager.beginTransaction().apply {

        f.apply {
            when (animate) {
                true -> {
                    setCustomAnimations(
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_left_exit
                    )
                }
            }

            when (f.replace) {
                true -> { replace(layoutId, fragment, tag)
                }
                false -> { add(layoutId, fragment, tag)
                }
            }
        }
    }


}