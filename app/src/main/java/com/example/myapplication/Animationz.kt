package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.res.TypedArray
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerLiteralTypeConstructor

object Animationz {

    private const val flipCardDuration = 1200L
    private const val flipCardDurationOneHalf = flipCardDuration/2
    private const val flipCardDurationOneThird = flipCardDuration/3
    const val flipCardDurationOneFourth = flipCardDuration/4

    fun View.flipToBackY(): ObjectAnimator { return ObjectAnimator.ofFloat(this, View.ROTATION_Y, 0f, 90f)}
    fun View.flipToFrontY(): ObjectAnimator { return ObjectAnimator.ofFloat(this, View.ROTATION_Y, -90f, 0f)}
    fun View.translateX(vararg float: Float): ObjectAnimator { return ObjectAnimator.ofFloat(this, View.TRANSLATION_X, *float)}
    fun View.sizeX(vararg float: Float): ObjectAnimator { return ObjectAnimator.ofFloat(this, View.SCALE_X, *float)}
    fun View.translateY(vararg float: Float): ObjectAnimator { return ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, *float)}

    fun View.aAlpha(from: Float, to: Float): ObjectAnimator {
        return ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat(View.ALPHA, from, to)
        )
    }

    fun View.alphaOutThenReverse(): ObjectAnimator {
        val v = this
        return v.aAlpha(from = 1.0f, to = 0.2f).apply {
            duration = flipCardDurationOneHalf
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = 1
        }
    }


    fun View.fadeInAnim1(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, View.ALPHA, 0.1f, 1f)
            .apply {
                interpolator = DecelerateInterpolator()
                duration = 300 }
    }
    fun View.fadeOutAnim1(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0.1f)
            .apply {
                interpolator = DecelerateInterpolator()
                duration = 300 }
    }

    fun View.slideOutRight(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, this.width + 300f).apply {
            interpolator = AccelerateInterpolator()
            duration = flipCardDurationOneFourth
        }
    }
    fun View.slideInLeft(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -this.width -300f, 0f).apply {
            interpolator = AccelerateInterpolator()
            duration = flipCardDurationOneFourth
        }
    }

    fun View.slideOutRightSlideInLeft(): AnimatorSet {
        val v = this
        return AnimatorSet().apply {
            playSequentially(v.slideOutRight(), v.slideInLeft())
        }
    }

    fun AppCompatTextView.slideOutRightInLeftSetText(sText: String): AnimatorSet {

        val v = this
        val anim1 = v.slideOutRight()
        val anim2 = v.slideInLeft()

        anim1.apply {
            duration = flipCardDurationOneFourth
        }.doOnEnd { _ ->
            v.text = sText
        }
        anim2.apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = flipCardDurationOneFourth
        }

        return AnimatorSet().apply {
            playSequentially(anim1, anim2)
        }

    }

    fun View.checkCameraDistance(targetScale: Float) {
        when (this.cameraDistance) {
            targetScale -> { }
            else -> {
                this.cameraDistance = targetScale
            }

        }
    }


}