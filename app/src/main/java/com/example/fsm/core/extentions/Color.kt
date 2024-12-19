package core.extensions

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.palette.graphics.Palette
import kotlin.math.roundToInt

/**
 * Escurece uma cor hexadecimal pelo fator fornecido.
 *
 * @param hexColor A cor hexadecimal a ser escurecida, no formato "#RRGGBB".
 * @param factor O fator de escurecimento, um valor flutuante entre 0.0 e 1.0.
 *             0.0 não terá efeito na cor original,
 *             enquanto 1.0 resultará em preto.
 * @return A cor hexadecimal escurecida, no formato "#RRGGBB".
 *
 * Uso:
 * val corOriginal = "#FF0000" // Vermelho
 * val corEscurecida = darkenColorOld(corOriginal, 0.2f) // Escurece em 20%
 * println(corEscurecida) // Saída: "#CC0000"
 */

fun darkenColorOld(hexColor: String, factor: Float): String {
    val r = Integer.valueOf(hexColor.substring(1, 3), 16)
    val g = Integer.valueOf(hexColor.substring(3, 5), 16)
    val b = Integer.valueOf(hexColor.substring(5, 7), 16)

    val newR = (r * (1 - factor)).toInt()
    val newG = (g * (1 - factor)).toInt()
    val newB = (b * (1 - factor)).toInt()

    val finalColor = String.format("#%02X%02X%02X", newR.coerceIn(0, 255), newG.coerceIn(0, 255), newB.coerceIn(0, 255))

    return finalColor
}

/**
 * Ajusta o brilho de uma cor hexadecimal para um valor alvo.
 *
 * @param hexColor A cor hexadecimal a ser ajustada, no formato "#RRGGBB".
 * @param targetBrightness O brilho alvo, um valor flutuante entre 0.0 (preto) e 1.0 (branco).
 * @return A cor hexadecimal com o brilho ajustado, no formato "#RRGGBB".
 *
 * Uso:
 * val corOriginal = "#FF0000" // Vermelho
 * val corAjustada = adjustBrightness(corOriginal, 0.5f) // Ajusta o brilho para 50%
 * println(corAjustada) // Saída: "#800000"
 */
fun adjustBrightness(hexColor: String, targetBrightness: Float): String {
    val r = Integer.valueOf(hexColor.substring(1, 3), 16)
    val g = Integer.valueOf(hexColor.substring(3, 5), 16)
    val b = Integer.valueOf(hexColor.substring(5, 7), 16)

    val currentBrightness = (0.299 * r + 0.587 * g + 0.114 * b) / 255

    val adjustFactor = targetBrightness / currentBrightness

    val newR = (r * adjustFactor).toInt().coerceIn(0, 255)
    val newG = (g * adjustFactor).toInt().coerceIn(0, 255)
    val newB = (b * adjustFactor).toInt().coerceIn(0, 255)

    val adjustedColor = String.format("#%02X%02X%02X", newR, newG, newB)

    return adjustedColor
}

/**
 * Clareia uma cor hexadecimal em uma porcentagem especificada.
 *
 * @param hexColor A cor hexadecimal a ser clareada, no formato "#RRGGBB".
 * @param percent A porcentagem para clarear a cor, um valor duplo entre 0.0 e 100.0.
 *               0.0 não terá efeito na cor original,
 *               enquanto 100.0 resultará em branco.
 * @return A cor hexadecimal clareada, no formato "#RRGGBB".
 *
 * Uso:
 * val corOriginal = "#FF0000" // Vermelho
 * val corClareada = lightenColor(corOriginal, 50.0) // Clareia em 50%
 * println(corClareada) // Saída: "#FF8080"
 */
fun lightenColor(hexColor: String, percent: Double): String {
    var color = hexColor.removePrefix("#")

    // Verifica se a cor possui canal alfa (8 caracteres)
    val hasAlpha = color.length == 8

    val alpha: Int
    val rgb: Int
    if (hasAlpha) {
        alpha = color.substring(0, 2).toInt(16)
        rgb = color.substring(2).toLong(16).toInt()
    } else {
        alpha = 255 // Assume opacidade total se não houver alfa
        rgb = color.toLong(16).toInt()
    }

    val red = rgb shr 16 and 0xFF
    val green = rgb shr 8 and 0xFF
    val blue = rgb and 0xFF

    val lightenAmount = percent / 100

    val lightenedRed = (red + ((255 - red) * lightenAmount)).toInt()
    val lightenedGreen = (green + ((255 - green) * lightenAmount)).toInt()
    val lightenedBlue = (blue + ((255 - blue) * lightenAmount)).toInt()

    val lightenedRGB = lightenedRed shl 16 or (lightenedGreen shl 8) or lightenedBlue

    val lightenedHexColor = if (hasAlpha) {
        String.format("#%02X%06X", alpha, lightenedRGB)
    } else {
        String.format("#%06X", 0xFFFFFF and lightenedRGB)
    }

    return lightenedHexColor
}

/*
fun lightenColor(hexColor: String, percent: Double): String {
    var color = hexColor.removePrefix("#")

    val rgb = color.toLong(16).toInt()

    val red = rgb shr 16 and 0xFF
    val green = rgb shr 8 and 0xFF
    val blue = rgb and 0xFF

    val lightenAmount = percent / 100

    val lightenedRed = (red + ((255 - red) * lightenAmount)).toInt()
    val lightenedGreen = (green + ((255 - green) * lightenAmount)).toInt()
    val lightenedBlue = (blue + ((255 - blue) * lightenAmount)).toInt()

    val lightenedRGB = lightenedRed shl 16 or (lightenedGreen shl 8) or lightenedBlue

    val lightenedHexColor = String.format("#%06X", 0xFFFFFF and lightenedRGB)

    return lightenedHexColor
}
*/
/**
 * Escurece uma cor hexadecimal em uma porcentagem especificada.
 *
 * @param hexColor A cor hexadecimal a ser escurecida, no formato "#RRGGBB".
 * @param percent A porcentagem para escurecer a cor, um valor duplo entre 0.0 e 100.0.
 *               0.0 não terá efeito na cor original,
 *               enquanto 100.0 resultará em preto.
 * @return A cor hexadecimal escurecida, no formato "#RRGGBB".
 *
 * Uso:
 * val corOriginal = "#FF0000" // Vermelho
 * val corEscurecida = darkenColor(corOriginal, 50.0) // Escurece em 50%
 * println(corEscurecida) // Saída: "#800000"
 */
fun darkenColor(hexColor: String, percent: Double): String {
    var color = hexColor.removePrefix("#")

    // Se a cor tiver 8 caracteres, inclui o alfa
    val hasAlpha = color.length == 8

    val alpha: Int
    val rgb: Int
    if (hasAlpha) {
        alpha = color.substring(0, 2).toInt(16)
        rgb = color.substring(2).toLong(16).toInt()
    } else {
        alpha = 255 // sem alfa explícito, assume opacidade total
        rgb = color.toLong(16).toInt()
    }

    val red = rgb shr 16 and 0xFF
    val green = rgb shr 8 and 0xFF
    val blue = rgb and 0xFF

    val darkenedRed = (red * (1 - percent / 100)).toInt()
    val darkenedGreen = (green * (1 - percent / 100)).toInt()
    val darkenedBlue = (blue * (1 - percent / 100)).toInt()

    val darkenedRGB = darkenedRed shl 16 or (darkenedGreen shl 8) or darkenedBlue

    val darkenedHexColor = if (hasAlpha) {
        String.format("#%02X%06X", alpha, darkenedRGB)
    } else {
        String.format("#%06X", 0xFFFFFF and darkenedRGB)
    }

    return darkenedHexColor
}
/*fun darkenColor(hexColor: String, percent: Double): String {
    var color = hexColor.removePrefix("#")

    val rgb = color.toLong(16).toInt()

    val red = rgb shr 16 and 0xFF
    val green = rgb shr 8 and 0xFF
    val blue = rgb and 0xFF

    val darkenedRed = (red * (1 - percent / 100)).toInt()
    val darkenedGreen = (green * (1 - percent / 100)).toInt()
    val darkenedBlue = (blue * (1 - percent / 100)).toInt()

    val darkenedRGB = darkenedRed shl 16 or (darkenedGreen shl 8) or darkenedBlue

    val darkenedHexColor = String.format("#%06X", 0xFFFFFF and darkenedRGB)

    return darkenedHexColor
}*/

/**
 * Encontra a cor mais brilhante em uma lista de cores hexadecimais.
 *
 * @param colors A lista de cores hexadecimais, no formato "#RRGGBB".
 * @return A cor hexadecimal mais brilhante da lista, ou nulo se a lista estiver vazia.
 *
 * Uso:
 * val cores = listOf("#FF0000", "#00FF00", "#0000FF") // Vermelho, Verde, Azul
 * val corMaisBrilante = brightestColor(cores)
 * println(corMaisBrilante) // Saída: "#00FF00" (Verde é a mais brilhante)
 */
fun brightestColor(colors: List<String>): String? {
    fun calculateBrightness(hexColor: String): Int {
        val r = Integer.parseInt(hexColor.substring(1, 3), 16)
        val g = Integer.parseInt(hexColor.substring(3, 5), 16)
        val b = Integer.parseInt(hexColor.substring(5, 7), 16)
        return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
    }

    if (colors.isEmpty()) return null

    var brightestColor = colors[0]
    var brightestBrightness = calculateBrightness(colors[0])

    for (i in 1 until colors.size) {
        val currentBrightness = calculateBrightness(colors[i])
        if (currentBrightness > brightestBrightness) {
            brightestColor = colors[i]
            brightestBrightness = currentBrightness
        }
    }

    return brightestColor
}

/**
 * Converte uma cor hexadecimal para um valor inteiro RGB.
 *
 * @param hex A cor hexadecimal a ser convertida, no formato "#RRGGBB" ou "RRGGBB".
 * @return O valor inteiro RGB correspondente à cor hexadecimal.
 *
 * Uso:
 * val corHexa = "#FF0000" // Vermelho
 * val corRgb = hexToRgb(corHexa)
 * println(corRgb) // Saída: 16711680
 */
fun hexToRgb(hex: String): Int {
    val color = Color.parseColor(hex)
    return Color.rgb(
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )
}



/**
 * Converte uma cor hexadecimal para um valor inteiro ARGB, com a opacidade especificada.
 *
 * @param opacityPercentage A porcentagem de opacidade, um valor inteiro entre 0 (totalmente transparente) e 100 (totalmente opaco).
 * @param hex A cor hexadecimal a ser convertida, no formato "#RRGGBB" ou "RRGGBB".
 * @return O valor inteiro ARGB correspondente à cor hexadecimal e à opacidade especificada.
 *
 * Uso:
 * val corHexa = "#FF0000" // Vermelho
 * val corArgb = hexToArgb(50, corHexa) // 50% de opacidade
 * println(corArgb) // Saída: 838860800
 */
fun hexToArgb(opacityPercentage: Int, hex: String): Int {
    val tempAlpha = (256 * opacityPercentage) / 100
    val color = Color.parseColor(hex)
    return Color.argb(
        tempAlpha,
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )
}

fun hexWithOpacity(opacityPercentage: Int, hex: String): String {
    val safeOpacity = opacityPercentage.coerceIn(0, 100)
    val tempAlpha = (255 * safeOpacity) / 100
    val color = Color.parseColor(hex)

    return String.format(
        "#%02X%02X%02X%02X",
        tempAlpha,
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )
}

// Uso:
// val hexWithOpacity = hexWithOpacity(50, "#FF0000")





fun drawableGetPalletColors(drawable: Drawable, completion: ((Pair<String, String>) -> Unit)? = null) {
    var color1Hex = "191919"
    var color2Hex = "191919"
    if (drawable is BitmapDrawable) {
        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        Palette.from(bitmap).generate { palette ->
            val color1 = palette?.darkVibrantSwatch?.rgb ?: 0
            color1Hex = if (color1 != 0) {
                String.format("%06X", (0xFFFFFF and color1))
            } else {
                "191919"
            }
            val color2 = palette?.mutedSwatch?.rgb ?: 0
            color2Hex = if (color2 != 0) {
                String.format("%06X", (0xFFFFFF and color2))
            } else {
                "191919"
            }
            completion?.invoke(Pair(color1Hex, color2Hex))
        }
    } else {
        completion?.invoke(Pair(color1Hex, color2Hex))
    }
}