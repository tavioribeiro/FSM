package com.example.fsm.core.extentions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun loadBitmapFromUrl(url: String, context: Context): Bitmap? {
    return try {
        val request = ImageRequest.Builder(context)
            .data(url)
            .size(1024)
            .build()
        val result = (Coil.imageLoader(context).execute(request) as SuccessResult).drawable
        (result as BitmapDrawable).bitmap
    } catch (e: Exception) {
        null
    }
}
