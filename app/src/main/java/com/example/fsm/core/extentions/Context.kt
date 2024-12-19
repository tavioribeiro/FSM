package com.example.fsm.core.extentions

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.example.fsm.R



// Extensão para converter de dp para pixels
fun Context.dpToPx(dp: Float): Int {
    val metrics = this.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
}



val colorsMap = mapOf(
    0 to R.color.color0,
    1 to R.color.color1,
    2 to R.color.color2,
    3 to R.color.color3,
    4 to R.color.color4,
    5 to R.color.color5,
    6 to R.color.color6,
    7 to R.color.color7,
    8 to R.color.color8,
    9 to R.color.color9,
)


object ContextManager {
    lateinit var appContext: Context

    fun init(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    fun getGlobalContext(): Context {
        return appContext
    }

    fun getColorHex(from: Int): String {
        val colorInt = ContextCompat.getColor(appContext, colorsMap[from]!!)
        return String.format("#%08X", colorInt)
    }
}