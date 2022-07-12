package com.groupphoto.app.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.transition.Transition
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

val TEXT_SMOOTH_ANIMTION_COEF = 1
fun animateColor(
        context: Context,
        target: Any?,
        propertyName: String,
        @ColorRes colorFrom: Int,
        @ColorRes colorTo: Int,
        duration: Int = 400,
        callback: () -> Unit
) {
    val anim = ObjectAnimator.ofObject(target, propertyName,
            ArgbEvaluator(), ContextCompat.getColor(context, colorFrom),
            ContextCompat.getColor(context, colorTo))
    anim.duration = duration.toLong()
    anim.addListener(animOnFinish { callback.invoke() })
    anim.start()
}

fun animOnFinish(callback: () -> Unit): AnimatorListenerAdapter {
    return object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            callback.invoke()
        }
    }
}

fun View.animTopToCenter(callback: () -> Unit): View {
    translationY = -50f
    alpha = 0f
    animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(400L * TEXT_SMOOTH_ANIMTION_COEF)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction { callback.invoke() }
            .start()
    return this
}

fun View.animLeftToCenter(callback: () -> Unit): View {
    translationX = -50f
    alpha = 0f
    animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(400L * TEXT_SMOOTH_ANIMTION_COEF)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction { callback.invoke() }
            .start()
    return this
}

fun View.animRightToCenter(callback: () -> Unit): View {
    translationX = 50f
    alpha = 0f
    animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(400L * TEXT_SMOOTH_ANIMTION_COEF)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction { callback.invoke() }
            .start()
    return this
}

fun View.animBottomToCenter(callback: () -> Unit): View {
    translationY = 50f
    alpha = 0f
    animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(400L * TEXT_SMOOTH_ANIMTION_COEF)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction { callback.invoke() }
            .start()
    return this
}

fun View.scaleWithBounceToLarge(duration: Long = 400L) {

    this.scaleX = 0f
    this.scaleY = 0f
    this.alpha = 0f
    this.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(duration).setInterpolator(OvershootInterpolator(0.5f)).start()
}

fun View.scaleWithBounceFromLarge() {
    this.scaleX = 3f
    this.scaleY = 3f
    this.alpha = 0f
    this.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(400).setInterpolator(OvershootInterpolator(0.5f)).start()
}

/**
animator.apply {
addListener(animOnFinish { /*some action*/ })
}
 */

@Suppress("unused")
fun animOnStart(callback: () -> Unit): AnimatorListenerAdapter {
    return object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            callback.invoke()
        }
    }
}

/**
animator.apply {
addListener(animOnStart { /*some action*/ })
}
 */

fun transitionListener(transitionStart: () -> Unit, transitionEnd: () -> Unit): Transition.TransitionListener {
    return object : Transition.TransitionListener {
        override fun onTransitionResume(transition: Transition?) {
        }

        override fun onTransitionPause(transition: Transition?) {
        }

        override fun onTransitionCancel(transition: Transition?) {
        }

        override fun onTransitionStart(transition: Transition?) {
            transitionStart.invoke()
        }

        override fun onTransitionEnd(transition: Transition?) {
            transitionEnd.invoke()
        }
    }
}
