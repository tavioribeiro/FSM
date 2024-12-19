package core.extensions

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView
import androidx.palette.graphics.Palette
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.animation.LinearInterpolator
import androidx.core.graphics.drawable.toBitmap
import com.example.fsm.core.extentions.ContextManager





private const val SKELETON_ANIMATOR_TAG = -123456
fun ImageView.startSkeletonAnimation(cornerRadius: Float = 20f) {
    val backgroundColor = 0xFF444444.toInt()
    val shimmerColor = 0xFF858585.toInt()

    val outerRadii = floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
    val roundedRectShape = RoundRectShape(outerRadii, null, null)

    this.background = ShapeDrawable(roundedRectShape).apply {
        paint.color = backgroundColor
    }

    val animator = ValueAnimator.ofFloat(-1f, 1f).apply {
        duration = 1500
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()

        addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            val translationX = fraction * width

            val animatedShader = LinearGradient(
                translationX, 0f, translationX + width, 0f,
                intArrayOf(backgroundColor, shimmerColor, backgroundColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            val roundedRectDrawable = ShapeDrawable(roundedRectShape).apply {
                paint.shader = animatedShader
            }

            this@startSkeletonAnimation.foreground = roundedRectDrawable
        }
    }

    this.setTag(SKELETON_ANIMATOR_TAG, animator)
    animator.start()
}

fun ImageView.stopSkeletonAnimation() {
    (this.getTag(SKELETON_ANIMATOR_TAG) as? ValueAnimator)?.let {
        it.cancel()
        this.foreground = null
        this.setTag(SKELETON_ANIMATOR_TAG, null)
    }
}









val defaultOriginalMatriz = floatArrayOf(
    1.1f, 0f, 0f, 0f, 0f,
    0f, 1.1f, 0f, 0f, 0f,
    0f, 0f, 1.1f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)
val defaultFinalMatriz = floatArrayOf(
    0.8f, 0f, 0f, 0f, 0f,
    0f, 0.8f, 0f, 0f, 0f,
    0f, 0f, 0.8f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)




/**
 * Aplica uma animação de overlay (sobreposição) a ImageView, clareando a imagem.
 *
 * Uso:
 * // Mostrar overlay com matrizes padrão e duração de 300ms.
 * myImageView.showOverlay()
 *
 * // Mostrar overlay com matrizes personalizadas.
 * myImageView.showOverlay(
 *     originalMatriz = floatArrayOf(...),
 *     finalMatriz = floatArrayOf(...)
 * )
 *
 * ```
 *
 * @param originalMatriz Matriz de cores original da imagem.
 * @param finalMatriz Matriz de cores final da animação.
 */
fun ImageView.showOverlay(
    originalMatriz: FloatArray = defaultOriginalMatriz,
    finalMatriz: FloatArray = defaultFinalMatriz
) {
    val animation = ValueAnimator.ofFloat(0f, 1f)
    animation.duration = 300

    animation.addUpdateListener { valueAnimator ->
        val fraction = valueAnimator.animatedFraction
        val interpolatedMatriz = FloatArray(originalMatriz.size)
        for (i in originalMatriz.indices) {
            interpolatedMatriz[i] =
                originalMatriz[i] + (finalMatriz[i] - originalMatriz[i]) * fraction
        }
        val matrizCor = ColorMatrix(interpolatedMatriz)
        val filtroCor = ColorMatrixColorFilter(matrizCor)
        this.colorFilter = filtroCor
    }
    animation.start()
}








/**
 * Remove a animação de overlay (sobreposição) da ImageView, restaurando a cor original da imagem.
 *
 * Uso:
 * // Remover overlay com matrizes padrão e duração de 300ms.
 * myImageView.hideOverlay()
 *
 * // Remover overlay com matrizes personalizadas.
 * myImageView.hideOverlay(
 *     originalMatriz = floatArrayOf(...),
 *     finalMatriz = floatArrayOf(...)
 * )
 *
 * ```
 *
 * @param originalMatriz Matriz de cores original da imagem.
 * @param finalMatriz Matriz de cores final da animação.
 */
fun ImageView.hideOverlay(
    originalMatriz: FloatArray = defaultOriginalMatriz,
    finalMatriz: FloatArray = defaultFinalMatriz
) {
    val animation = ValueAnimator.ofFloat(0f, 1f)
    animation.duration = 300

    animation.addUpdateListener { valueAnimator ->
        val fraction = valueAnimator.animatedFraction
        val interpolatedMatriz = FloatArray(originalMatriz.size)
        for (i in originalMatriz.indices) {
            interpolatedMatriz[i] =
                finalMatriz[i] + (originalMatriz[i] - finalMatriz[i]) * fraction
        }
        val matrizCor = ColorMatrix(interpolatedMatriz)
        val filtroCor = ColorMatrixColorFilter(matrizCor)
        this.colorFilter = filtroCor
    }
    animation.start()
}







/**
 * Reseta o Color Filter da ImageView para o estado final da animação de overlay.
 *
 * Uso:
 * // Reseta o Color Filter com a matriz padrão.
 * myImageView.resetColorFilter()
 *
 * // Reseta o Color Filter com uma matriz personalizada.
 * myImageView.resetColorFilter(finalMatriz = floatArrayOf(...))
 *
 * ```
 *
 * @param finalMatriz Matriz de cores final da animação de overlay.
 */
fun ImageView.resetColorFilter(finalMatriz: FloatArray = defaultFinalMatriz) {
    this.colorFilter = ColorMatrixColorFilter(finalMatriz)
}







/**
 * Obtém as cores da paleta da imagem na ImageView.
 *
 * Uso:
 * // Obtém as cores da paleta e imprime no Logcat.
 * myImageView.getPalletColors { (color1, color2) ->
 *     Log.d("Cores", "Cor 1: $color1, Cor 2: $color2")
 * }
 *
 * ```
 *
 * @param completion Função a ser executada após a extração das cores da paleta, recebendo um Pair com os códigos hexadecimais das cores.
 */
fun ImageView.getPalletColors(onCompletation: ((Pair<String, String>) -> Unit)? = null) {

    val defaultColor = ContextManager.getColorHex(1)
    var color1Hex = defaultColor
    var color2Hex = defaultColor
    if (this.drawable is BitmapDrawable) {
        val bitmap: Bitmap = (this.drawable as BitmapDrawable).bitmap
        Palette.from(bitmap).generate { palette ->

            val color1 = palette?.vibrantSwatch?.rgb ?: 0
            color1Hex = if (color1 != 0) {
                String.format("%06X", (0xFFFFFF and color1))
            } else {
                defaultColor
            }

            val color2 = palette?.mutedSwatch?.rgb ?: 0
            color2Hex = if (color2 != 0) {
                String.format("%06X", (0xFFFFFF and color2))
            } else {
                defaultColor
            }
            onCompletation?.invoke(Pair("#$color1Hex", "#$color2Hex"))
        }
    } else {
        onCompletation?.invoke(Pair("#$color1Hex", "#$color2Hex"))
    }
}



fun ImageView.getDominantColor(completion: ((String) -> Unit)? = null) {
    var dominantColorHex = "191919" // Default color if no dominant color is found
    if (this.drawable is BitmapDrawable) {
        val bitmap: Bitmap = (this.drawable as BitmapDrawable).bitmap
        Palette.from(bitmap).generate { palette ->
            val dominantColor = palette?.dominantSwatch?.rgb ?: 0
            dominantColorHex = if (dominantColor != 0) {
                String.format("%06X", (0xFFFFFF and dominantColor))
            } else {
                "191919"
            }
            completion?.invoke(dominantColorHex)
        }
    } else {
        completion?.invoke(dominantColorHex)
    }
}



fun ImageView.getBitmap(): Bitmap? {
    val drawable = this.drawable
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        null
    }
}


fun ImageView.getBrightestColor(completion: ((String?) -> Unit)? = null) {
    if (this.drawable is BitmapDrawable) {
        val bitmap: Bitmap = (this.drawable as BitmapDrawable).bitmap
        Palette.from(bitmap).generate { palette ->
            val swatches = listOf(
                palette?.vibrantSwatch,
                palette?.darkVibrantSwatch,
                palette?.lightVibrantSwatch,
                palette?.mutedSwatch,
                palette?.darkMutedSwatch,
                palette?.lightMutedSwatch
            ).filterNotNull()

            val colors = swatches.map { swatch ->
                String.format("#%06X", (0xFFFFFF and swatch.rgb))
            }

            val brightestColor = brightestColor(colors)
            completion?.invoke(brightestColor)
        }
    } else {
        completion?.invoke(null)
    }
}










fun Drawable.resize(width: Int, height: Int, resources: Resources): Drawable {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    return BitmapDrawable(resources, bitmap)
}




fun Drawable.cropCenterSection(widthDp: Int, heightDp: Int, resources: Resources): BitmapDrawable {
    // Converter DP para pixels
    val widthPx = (widthDp * resources.displayMetrics.density).toInt()
    val heightPx = (heightDp * resources.displayMetrics.density).toInt()

    // Obter o bitmap do Drawable
    val bitmap = if (this is BitmapDrawable) {
        this.bitmap
    } else {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888).also {
            val canvas = Canvas(it)
            this.setBounds(0, 0, canvas.width, canvas.height)
            this.draw(canvas)
        }
    }

    // Calcular as coordenadas do recorte central
    val left = ((bitmap.width - widthPx) / 2).coerceAtLeast(0)
    val top = ((bitmap.height - heightPx) / 2).coerceAtLeast(0)
    val right = (left + widthPx).coerceAtMost(bitmap.width)
    val bottom = (top + heightPx).coerceAtMost(bitmap.height)

    // Criar o bitmap recortado
    val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)

    // Retornar como um BitmapDrawable
    return BitmapDrawable(resources, croppedBitmap)
}




fun Drawable.getSizes(callback: (width: Int, height: Int) -> Unit) {
    val width = intrinsicWidth
    val height = intrinsicHeight
    callback(width, height)
}





fun Drawable.toTopRoundedDrawable(radius: Float): BitmapDrawable {
    val bitmap = if (this is BitmapDrawable) {
        this.bitmap
    } else {

        val width = this.intrinsicWidth
        val height = this.intrinsicHeight

        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bmp ->
            val canvas = Canvas(bmp)
            this.setBounds(0, 0, width, height)
            this.draw(canvas)
        }
    }

    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    val path = Path().apply {
        addRoundRect(rect, floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f), Path.Direction.CW)
    }

    canvas.drawPath(path, paint)
    return BitmapDrawable(Resources.getSystem(), output)
}




fun Drawable.toAllRoundedDrawable(radius: Float): BitmapDrawable {
    val bitmap = if (this is BitmapDrawable) {
        this.bitmap
    } else {
        // Criar um bitmap com as dimensões originais do drawable
        val width = this.intrinsicWidth
        val height = this.intrinsicHeight

        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bmp ->
            val canvas = Canvas(bmp)
            this.setBounds(0, 0, width, height)
            this.draw(canvas)
        }
    }

    // Criar um novo bitmap com as mesmas dimensões do original
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    val path = Path().apply {
        addRoundRect(rect, radius, radius, Path.Direction.CW)
    }

    canvas.drawPath(path, paint)
    return BitmapDrawable(Resources.getSystem(), output) // Adiciona Resources para manter a escala
}


