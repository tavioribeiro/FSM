package core.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.fsm.R
import com.example.fsm.core.extentions.ContextManager
import com.example.fsm.core.extentions.dpToPx
import java.util.concurrent.atomic.AtomicLong




fun View.genId(minValue: Int = 0): Int {
    var generatedId: Int
    do {
        generatedId = View.generateViewId()
    } while (generatedId <= minValue)
    this.id = generatedId
    return generatedId
}
/**
 * Esconde o teclado virtual se ele estiver sendo exibido para a view atual.
 * Esta função é útil para esconder o teclado ao tocar fora de um campo de edição de texto,
 * por exemplo.
 *
 * Uso:
 * myEditText.hideKeyboard()
 * ```
 *
 * @return True se a requisição para esconder o teclado foi bem sucedida, False caso contrário.
 */
fun View.hideKeyboard(): Boolean {
    try {
        // Obtém o serviço InputMethodManager do sistema.
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Solicita ao InputMethodManager para esconder o teclado.
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
        // Captura qualquer exceção RuntimeException, que pode ocorrer se o teclado não estiver visível.
        // Retorna false em caso de erro.
    }
    return false
}

/**
 * Exibe o teclado virtual para a view atual.
 * Esta função é útil para mostrar o teclado automaticamente ao abrir um campo de edição de texto,
 * por exemplo.
 *
 * Uso:
 * myEditText.showKeyboard()
 * ```
 */
fun View.showKeyboard() {
    // Obtém o serviço InputMethodManager do sistema.
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // Define o foco na view atual.
    this.requestFocus()
    // Solicita ao InputMethodManager para mostrar o teclado.
    imm.showSoftInput(this, 0)
}

/**
 * Torna a view visível. Equivalente a definir a visibilidade como View.VISIBLE.
 *
 * Uso:
 * myView.show()
 * ```
 */
fun View.show(){
    this.visibility = View.VISIBLE
}

/**
 * Torna a view invisível, mas ainda ocupa espaço no layout.
 * Equivalente a definir a visibilidade como View.INVISIBLE.
 *
 * Uso:
 * myView.hide()
 * ```
 */
fun View.hide() {
    this.visibility = View.INVISIBLE
}

/**
 * Torna a view invisível e não ocupa espaço no layout.
 * Equivalente a definir a visibilidade como View.GONE.
 *
 * Uso:
 * myView.gone()
 * ```
 */
fun View.gone(){
    this.visibility = View.GONE
}

/**
 * Aplica uma animação de fade out (desaparecimento gradual) à view.
 *
 * Uso:
 * // Fade out com duração padrão (500ms) e visibilidade INVISIBLE após a animação.
 * myView.fadeOutAnimation()
 *
 * // Fade out com duração de 1 segundo e visibilidade GONE após a animação.
 * myView.fadeOutAnimation(duration = 1000, visibility = View.GONE)
 *
 * // Fade out com callback executado após a animação.
 * myView.fadeOutAnimation {
 *     // Faça algo após a animação.
 * }
 * ```
 *
 * @param duration Duração da animação em milissegundos. Padrão: 500ms.
 * @param visibility Visibilidade da view após a animação.
 *                   Pode ser View.VISIBLE, View.INVISIBLE ou View.GONE.
 *                   Padrão: View.INVISIBLE.
 * @param completion Função a ser executada após o término da animação (opcional).
 */
fun View.fadeOutAnimation(duration: Long = 500, visibility: Int = View.INVISIBLE, completion: (() -> Unit)? = null) {
    animate()
        .alpha(0f) // Define o valor alfa para 0 (completamente transparente).
        .setDuration(duration) // Define a duração da animação.
        .withEndAction { // Define uma ação a ser executada após a animação.
            this.visibility = visibility // Define a visibilidade da view após a animação.
            completion?.let {
                it() // Executa a função de callback, se fornecida.
            }
        }
}


fun View.fadeOutAnimationNoRepeat(duration: Long = 500, visibility: Int = View.INVISIBLE, completion: (() -> Unit)? = null) {
    if (this.visibility == View.VISIBLE) {
        animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                this.visibility = visibility
                completion?.let { it() }
            }
    } else {
        // View já está invisível ou gone, não precisa fazer fade out
        this.visibility = visibility // Define a visibilidade para o valor desejado
        completion?.let { it() }
    }
}


/**
 * Aplica uma animação de fade in (aparecimento gradual) à view.
 *
 * Uso:
 * // Fade in com duração padrão (500ms).
 * myView.fadeInAnimation()
 *
 * // Fade in com duração de 1 segundo.
 * myView.fadeInAnimation(duration = 1000)
 *
 * // Fade in com callback executado após a animação.
 * myView.fadeInAnimation {
 *     // Faça algo após a animação.
 * }
 * ```
 *
 * @param duration Duração da animação em milissegundos. Padrão: 500ms.
 * @param completion Função a ser executada após o término da animação (opcional).
 */
fun View.fadeInAnimation(duration: Long = 500, completion: (() -> Unit)? = null) {
    alpha = 0f // Define o valor alfa inicial para 0 (completamente transparente).
    visibility = View.VISIBLE // Define a visibilidade para VISIBLE antes da animação.
    animate()
        .alpha(1f) // Define o valor alfa para 1 (completamente opaco).
        .setDuration(duration) // Define a duração da animação.
        .withEndAction { // Define uma ação a ser executada após a animação.
            completion?.let {
                it() // Executa a função de callback, se fornecida.
            }
        }
}

fun View.fadeInAnimationNoRepeat(duration: Long = 500, completion: (() -> Unit)? = null) {
    if (visibility == View.GONE || visibility == View.INVISIBLE) {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(duration)
            .withEndAction {
                completion?.let { it() }
            }
    } else {
        // View já está visível, não precisa fazer fade in
        completion?.let { it() }
    }
}


/**
 * Define a largura da view em dp (Density-independent Pixels).
 * Útil para definir a largura da view de forma independente da densidade de pixels da tela.
 *
 * Uso:
 * myView.setWidthInDp(widthInDp = 100f)
 * ```
 *
 * @param widthInDp Largura da view em dp.
 */
fun View.setWidthInDp(widthInDp: Float) {
    val layoutParams = this.layoutParams ?: return

    layoutParams.width = context.dpToPx(widthInDp)

    this.layoutParams = layoutParams
    this.requestLayout()
}

/**
 * Define o estilo de fundo da view usando um GradientDrawable.
 * Permite definir a cor de fundo, raio dos cantos, espessura da borda e cor da borda.
 *
 * Uso:
 * // Fundo vermelho com cantos arredondados de 10dp, borda preta de 2dp.
 * myView.styleBackground(backgroundColor = "#FF0000", radius = 10f, borderWidth = 2, borderColor = "#000000")
 *
 * // Fundo transparente com cantos superiores arredondados de 15dp.
 * myView.styleBackground(backgroundColor = "#00000000", topLeftRadius = 15f, topRightRadius = 15f)
 * ```
 *
 * @param backgroundColor Cor de fundo em formato hexadecimal (ex: "#FF0000").
 *                        Padrão: transparente ("#00FFFFFF").
 * @param radius Raio dos cantos em pixels. Este valor será usado para todos os cantos, a menos que
 *               valores específicos sejam fornecidos para topLeftRadius, topRightRadius, bottomLeftRadius
 *               e bottomRightRadius. Padrão: 0f (sem cantos arredondados).
 * @param borderWidth Espessura da borda em pixels. Padrão: 0 (sem borda).
 * @param borderColor Cor da borda em formato hexadecimal. Padrão: transparente ("#00FFFFFF").
 * @param topLeftRadius Raio do canto superior esquerdo em pixels. Padrão: igual ao valor de `radius`.
 * @param topRightRadius Raio do canto superior direito em pixels. Padrão: igual ao valor de `radius`.
 * @param bottomLeftRadius Raio do canto inferior esquerdo em pixels. Padrão: igual ao valor de `radius`.
 * @param bottomRightRadius Raio do canto inferior direito em pixels. Padrão: igual ao valor de `radius`.
 */
fun View.styleBackground(
    backgroundColor: String? = "#00FFFFFF",
    backgroundColorsList: List<String>? = null,
    orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.BL_TR,
    radius: Float = 0f,
    borderWidth: Int = 0,
    borderColor: String? = "#00FFFFFF",
    topLeftRadius: Float = radius,
    topRightRadius: Float = radius,
    bottomLeftRadius: Float = radius,
    bottomRightRadius: Float = radius,
    widthInDp: Float? = null,
    heightInDp: Float? = null,
    marginInDp: Float? = null,
    paddingInDp: Float? = null,
    marginStartInDp: Float? = null,
    marginTopInDp: Float? = null,
    marginEndInDp: Float? = null,
    marginBottomInDp: Float? = null,
    paddingStartInDp: Float? = null,
    paddingTopInDp: Float? = null,
    paddingEndInDp: Float? = null,
    paddingBottomInDp: Float? = null
) {
    val shape = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE

        // Configura o gradiente ou cor de fundo
        if (backgroundColorsList != null && backgroundColorsList.isNotEmpty()) {
            colors = backgroundColorsList.map { Color.parseColor(it) }.toIntArray()
            this.orientation = orientation
        } else {
            setColor(Color.parseColor(backgroundColor))
        }

        // Configura os cantos arredondados
        cornerRadii = floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        )

        // Configura a borda
        setStroke(borderWidth, Color.parseColor(borderColor))
    }

    // Aplica o GradientDrawable como background
    this.background = shape

    // Configura as dimensões da View
    val density = context.resources.displayMetrics.density
    widthInDp?.let { layoutParams.width = (it * density).toInt() }
    heightInDp?.let { layoutParams.height = (it * density).toInt() }

    // Configura margens
    val layoutParams = this.layoutParams as? ViewGroup.MarginLayoutParams
    layoutParams?.let {
        marginInDp?.let {
            val marginPx = (it * density).toInt()
            layoutParams.setMargins(marginPx, marginPx, marginPx, marginPx)
        }
        marginStartInDp?.let { layoutParams.marginStart = (it * density).toInt() }
        marginTopInDp?.let { layoutParams.topMargin = (it * density).toInt() }
        marginEndInDp?.let { layoutParams.marginEnd = (it * density).toInt() }
        marginBottomInDp?.let { layoutParams.bottomMargin = (it * density).toInt() }
    }

    // Configura paddings
    val paddingStartPx = paddingStartInDp?.let { (it * density).toInt() } ?: this.paddingStart
    val paddingTopPx = paddingTopInDp?.let { (it * density).toInt() } ?: this.paddingTop
    val paddingEndPx = paddingEndInDp?.let { (it * density).toInt() } ?: this.paddingEnd
    val paddingBottomPx = paddingBottomInDp?.let { (it * density).toInt() } ?: this.paddingBottom
    val paddingPx = paddingInDp?.let { (it * density).toInt() }
    this.setPadding(
        paddingPx ?: paddingStartPx,
        paddingPx ?: paddingTopPx,
        paddingPx ?: paddingEndPx,
        paddingPx ?: paddingBottomPx
    )
}
// Função auxiliar para definir altura em DP.
fun View.setHeightInDp(heightInDp: Float) {
    layoutParams = (layoutParams ?: ViewGroup.LayoutParams(0, 0)).apply {
        height = context.dpToPx(heightInDp)
    }
    requestLayout()
}






fun View.changeBackgroundColorWithGradient(
    color1: String,
    color2: String,
    originalColor1 : String = ContextManager.getColorHex(0),
    originalColor2 : String = ContextManager.getColorHex(1)
) {
    // Obtenção das cores atuais a partir da lógica existente (ou você pode passar como parâmetros, se necessário)
    val startColors = intArrayOf(
        Color.parseColor(originalColor1),
        Color.parseColor(originalColor2)
    )

    val endColors = intArrayOf(
        Color.parseColor(color1),
        Color.parseColor(color2)
    )

    // Criar o GradientDrawable
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.BL_TR,
        startColors
    )

    // Obter displayMetrics para cálculo da posição do centro e raio do gradiente
    val displayMetrics: DisplayMetrics = this.resources.displayMetrics
    val gradientCenter = 500f / (displayMetrics.widthPixels / displayMetrics.density)

    // Definir o ponto central do gradiente
    gradientDrawable.setGradientCenter(gradientCenter, 0.5f)

    // Definir o raio do gradiente em pixels
    val gradientRadius = 500 * displayMetrics.density
    gradientDrawable.gradientRadius = gradientRadius

    // Aplicar o fundo com o gradiente
    this.background = gradientDrawable

    // Definir animação para transição das cores
    val colorAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1500
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            val currentColors = startColors.indices.map { i ->
                ArgbEvaluator().evaluate(animatedValue, startColors[i], endColors[i]) as Int
            }.toIntArray()

            gradientDrawable.colors = currentColors
            this@changeBackgroundColorWithGradient.background = gradientDrawable
        }
    }

    // Iniciar a animação
    colorAnimation.start()
}




/**
 * Define um gradiente de fundo para a view usando um GradientDrawable.
 * O gradiente é definido com duas cores e orientação diagonal (bottom-left para top-right).
 *
 * Uso:
 * // Gradiente de vermelho para azul.
 * myView.backgroundColorChangerGradient2(color1 = "#FF0000", color2 = "#0000FF")
 * ```
 *
 * @param color1 Primeira cor do gradiente em formato hexadecimal.
 * @param color2 Segunda cor do gradiente em formato hexadecimal.
 */
fun View.backgroundColorChangerGradient2(
    color1: String,
    color2: String,
) {
    // Define as cores do gradiente.
    val startColors = intArrayOf(
        Color.parseColor(color1),
        Color.parseColor(color2)
    )

    // Cria um novo GradientDrawable com orientação diagonal.
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.BL_TR,
        startColors,
    )

    // Define o raio dos cantos (opcional).
    gradientDrawable.cornerRadius = 15f
    // Define o GradientDrawable como background da view.
    this.background = gradientDrawable
}

/**
 * Anima a escala de uma view para um determinado fator.
 * A view será escalada uniformemente nos eixos X e Y.
 *
 * Uso:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.animateScale(1.08f) // Usa a duração padrão de 200ms
 * myView.animateScale(1.5f, 500) // Define a duração para 500ms
 * ```
 *
 * @param scaleFactor O fator de escala desejado.
 * @param duration A duração da animação em milissegundos (padrão: 200ms).
 */
fun View.animateScale(scaleFactor: Float, duration: Long = 200) {
    val scaleAnimator = ValueAnimator.ofFloat(scaleX, scaleFactor)
    scaleAnimator.duration = duration
    scaleAnimator.addUpdateListener { animation ->
        val value = animation.animatedValue as Float
        scaleX = value
        scaleY = value
    }
    scaleAnimator.start()
}





/**
 * Define o padding de uma view usando valores em dp.
 * O padding vertical e horizontal é aplicado igualmente em todos os lados.
 *
 * Uso:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.setPadding2(context, 16, 8) // Define padding vertical de 16dp e horizontal de 8dp
 * ```
 *
 * @param context O contexto da aplicação.
 * @param vertical O valor do padding vertical em dp.
 * @param horizontal O valor do padding horizontal em dp.
 */
fun View.setPadding2(context: Context, vertical: Int, horizontal:Int){
    val paddingLeftPx = context.dpToPx(horizontal.toFloat())
    val paddingTopPx = context.dpToPx(vertical.toFloat())
    val paddingRightPx = context.dpToPx(horizontal.toFloat())
    val paddingBottomPx = context.dpToPx(vertical.toFloat())

    this.setPadding(paddingLeftPx, paddingTopPx, paddingRightPx, paddingBottomPx)
}

/**
 * Define a margem de uma view usando valores em dp.
 * A margem vertical e horizontal é aplicada igualmente em todos os lados.
 *
 * Uso:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.setMargin(context, 16, 8) // Define margem vertical de 16dp e horizontal de 8dp
 * ```
 *
 * @param context O contexto da aplicação.
 * @param vertical O valor da margem vertical em dp.
 * @param horizontal O valor da margem horizontal em dp.
 */
fun View.setMargin(context: Context, vertical: Int, horizontal:Int){
    val marginLeftPx = context.dpToPx(horizontal.toFloat())
    val marginTopPx = context.dpToPx(vertical.toFloat())
    val marginRightPx = context.dpToPx(horizontal.toFloat())
    val marginBottomPx = context.dpToPx(vertical.toFloat())

    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(marginLeftPx, marginTopPx, marginRightPx, marginBottomPx)
    this.layoutParams = layoutParams
}

/**
 * Define a largura de uma view usando um valor em dp.
 *
 * Uso:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.setWidth(context, 100) // Define a largura da view para 100dp
 * ```
 *
 * @param context O contexto da aplicação.
 * @param size O valor da largura em dp.
 */
fun View.setWidth(context: Context, size: Int) {
    val widthPx = context.dpToPx(size.toFloat())
    val layoutParams = this.layoutParams

    layoutParams.width = widthPx
    this.layoutParams = layoutParams
}

/**
 * Define a altura de uma view usando um valor em dp.
 *
 * Uso:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.setHeight(context, 150) // Define a altura da view para 150dp
 * ```
 *
 * @param context O contexto da aplicação.
 * @param size O valor da altura em dp.
 */
fun View.setHeight(context: Context, size: Int) {
    val heightPx = context.dpToPx(size.toFloat())
    val layoutParams = this.layoutParams

    layoutParams.height = heightPx
    this.layoutParams = layoutParams
}




fun View.blockDPadAction(keycode:Int){
    this.setOnKeyListener(object : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == keycode) {
                    return true
                }
            }
            return false
        }
    })
}


fun View.blockDPadActions(keycodes: MutableList<Int>) {
    this.setOnKeyListener(object : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN) {
                if (keycodes.contains(keyCode)) {
                    return true // Bloqueia a ação se o keyCode estiver na lista
                }
            }
            return false
        }
    })
}





fun View.blockDpadForWhile(interval: Long) {
    // Disable D-pad actions
    this.setOnKeyListener { _, keyCode, _ ->
        return@setOnKeyListener keyCode in KeyEvent.KEYCODE_DPAD_UP..KeyEvent.KEYCODE_DPAD_RIGHT
    }

    // Enable D-pad actions after x ms
    Handler(Looper.getMainLooper()).postDelayed({
        this.setOnKeyListener(null)
    }, interval)
}





fun View.dpadEvent3(callback: ((keycode: Int) -> Boolean)? = null) {
    this.setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.ACTION_UP) {
            if (callback != null) { // Verifica se o D-Pad está bloqueado
                return@setOnKeyListener callback(keyCode)
            }
        }
        return@setOnKeyListener false
    }
}




fun View.blockDpadInitiallyThenHandle(interval: Long, callback: ((keycode: Int) -> Boolean)? = null) {
    var isBlocked = true

    // Bloqueia inicialmente as ações do D-pad
    this.setOnKeyListener { _, keyCode, _ ->
        return@setOnKeyListener isBlocked && keyCode in KeyEvent.KEYCODE_DPAD_UP..KeyEvent.KEYCODE_DPAD_RIGHT
    }

    // Libera as ações do D-pad após o intervalo, e passa o controle para o callback
    Handler(Looper.getMainLooper()).postDelayed({
        isBlocked = false
        this.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.ACTION_UP) {
                if (callback != null) {
                    return@setOnKeyListener callback(keyCode)
                }
            }
            return@setOnKeyListener false
        }
    }, interval)
}




fun View.zoomIn(duration: Long = 300, factor: Float = 1.5f) {
    val scale = ScaleAnimation(
        1f, factor,
        1f, factor,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    scale.duration = duration
    scale.fillAfter = true
    this.startAnimation(scale)
}

fun View.zoomOut(duration: Long = 300, factor: Float = 1.5f) {
    val scale = ScaleAnimation(
        factor, 1f,
        factor, 1f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    scale.duration = duration
    scale.fillAfter = true
    this.startAnimation(scale)
}




fun View.getWidth(onWidthReady: (Int) -> Unit) {
    if (width > 0) {
        onWidthReady(width)
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onWidthReady(width)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}



fun View.getHeight(onHeightReady: (Int) -> Unit) {
    if (height > 0) {
        // Se a altura já estiver disponível, retorna imediatamente
        onHeightReady(height)
    } else {
        // Caso contrário, espera até o layout ser medido
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Chama a função passando a altura
                onHeightReady(height)
                // Remove o listener após obter a altura
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}



fun View.getSizes(onSizeReady: (Int, Int) -> Unit) {
    if (width > 0 && height > 0) {
        // Se a largura e altura já estiverem disponíveis, retorna imediatamente
        onSizeReady(width, height)
    } else {
        // Caso contrário, espera até o layout ser medido
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Chama a função passando a largura e altura
                onSizeReady(width, height)
                // Remove o listener após obter os valores
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}
