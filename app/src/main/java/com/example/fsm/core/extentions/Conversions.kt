package core.extensions

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

/*
fun dpToPx(dp: Float): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
}

fun dpToPx2(context: Context, dp:Int):Int{
    val resources: Resources = context.resources

    val displayMetrics: DisplayMetrics = resources.displayMetrics
    val density: Float = displayMetrics.density

    val pixels: Int = (dp * density).toInt()

    return pixels
}

fun dpToPx3(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density).toInt()
}

*/




fun fromPercentToValue(baseValue:Int, percentValue:Int): Float {
    return (baseValue*percentValue/100).toFloat()
}

