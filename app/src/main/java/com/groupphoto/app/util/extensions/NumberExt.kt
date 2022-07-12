package com.groupphoto.app.util.extensions

import android.content.Context

/**
 * Convert pixel to density pixel value
 */
fun Number.pxToDp(context: Context) = this.toFloat() / context.resources.displayMetrics.density

/**
 * Convert density pixel to pixel value
 */
fun Number.dpToPx(context: Context) = this.toFloat() * context.resources.displayMetrics.density

/**
 * Convert pixel to scaled pixel value
 */
fun Number.pxToSp(context: Context) = this.toFloat() / context.resources.displayMetrics.scaledDensity

/**
 * Convert scaled pixel to pixel value
 */
fun Number.spToPx(context: Context) = this.toFloat() * context.resources.displayMetrics.scaledDensity