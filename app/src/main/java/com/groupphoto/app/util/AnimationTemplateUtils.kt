package com.groupphoto.app.util

import android.view.View
import android.view.animation.OvershootInterpolator


object AnimationTemplateUtils {
    fun animateStepByStepVisible(views: Array<View>) {
        for ((countOfAnimatedView, view) in views.withIndex()) {
            view.alpha = 0f
            view.scaleX = 0.8f
            view.scaleY = 0.8f
            view.animate().alpha(1f).scaleY(1f).scaleX(1f)
                .setStartDelay((70 * countOfAnimatedView).toLong()).setDuration(300)
                .setInterpolator(OvershootInterpolator()).start()
        }
    }
}
