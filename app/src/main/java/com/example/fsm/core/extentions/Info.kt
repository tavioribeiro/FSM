package com.example.fsm.core.extentions

import android.content.Context
import com.example.fsm.core.utils.log

fun getScreenDpiAndResolution(context: Context){
    val displayMetrics = context.resources.displayMetrics

    // Obtém o DPI da tela
    val dpi = displayMetrics.densityDpi

    // Obtém a resolução da tela em pixels
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels

    log("---- Screen Info ----")
    log("DPI: $dpi")
    log("Largura: $widthPixels")
    log("Altura: $heightPixels")
    log("----------------------")
}