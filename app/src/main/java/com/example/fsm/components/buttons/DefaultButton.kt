package com.example.fsm.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.example.fsm.R
import com.example.fsm.core.extentions.ContextManager
import com.example.fsm.core.extentions.dpToPx
import com.example.fsm.core.utils.log
import com.example.fsm.databinding.CLayoutDefaultButtonBinding
import core.extensions.blockDPadActions
import core.extensions.darkenColor
import core.extensions.fadeInAnimationNoRepeat
import core.extensions.fadeOutAnimationNoRepeat
import core.extensions.genId
import core.extensions.lightenColor
import core.extensions.setHeightInDp
import core.extensions.setWidthInDp
import core.extensions.styleBackground


class DefaultButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @DrawableRes private var icon: Int? = null,
    private var label: String = "",
    private var action: () -> Unit = {},
    private var onFocus: (Int) -> Unit = {},
    private var width: Float? = null,
    private var backgroundColor: String? = null,
    private var labelColor: String? = null,
    private var borderColorOnFocus: String? = null,
    private var iconColor: String? = null,
    private var blockActions: MutableList<Int> = mutableListOf(),
    private var nextFocusLeftId: Int = 0,
    private var nextFocusRightId: Int = 0,
    private var nextFocusUpId: Int = 0,
    private var nextFocusDownId: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: CLayoutDefaultButtonBinding = CLayoutDefaultButtonBinding.inflate(LayoutInflater.from(context), this, true)
    private var onClickListener: (() -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DefaultButton,
            0, 0
        ).apply {
            try {
                // Prioriza valores do construtor, caso não definidos, usa os do XML
                icon = icon ?: getResourceId(R.styleable.DefaultButton_icon, 0)
                label = label.ifEmpty { getString(R.styleable.DefaultButton_label) ?: "" }
                width = width ?: getFloat(R.styleable.DefaultButton_width, 0f)
                backgroundColor = backgroundColor ?: getString(R.styleable.DefaultButton_bgdColor) ?: ContextManager.getColorHex(2)
                labelColor = labelColor ?: getString(R.styleable.DefaultButton_labelColor) ?: ContextManager.getColorHex(5)
                borderColorOnFocus = borderColorOnFocus ?: getString(R.styleable.DefaultButton_borderColorOnFocus) ?: ContextManager.getColorHex(5)
                iconColor = iconColor ?: getString(R.styleable.DefaultButton_iconColor)
                nextFocusLeftId = if (nextFocusLeftId == 0) getResourceId(R.styleable.DefaultButton_nextFocusLeft, 0) else nextFocusLeftId
                nextFocusRightId = if (nextFocusRightId == 0) getResourceId(R.styleable.DefaultButton_nextFocusRight, 0) else nextFocusRightId
                nextFocusUpId = if (nextFocusUpId == 0) getResourceId(R.styleable.DefaultButton_nextFocusUp, 0) else nextFocusUpId
                nextFocusDownId = if (nextFocusDownId == 0) getResourceId(R.styleable.DefaultButton_nextFocusDown, 0) else nextFocusDownId

                val blockActionsString = getString(R.styleable.DefaultButton_blockActions)
                blockActionsString?.split(",")?.map { it.trim().toIntOrNull() }?.filterNotNull()?.let {
                    if (blockActions.isEmpty()) blockActions.addAll(it)
                }
            } finally {
                recycle()
            }
        }

        setupComponent()
    }

    //Configuração inicial do componente
    private fun setupComponent() {
        binding.mainContainer.genId(500)

        this.configureLabel()
        this.configureIcon()
        this.configureLoading()
        this.configureFocusNavigation()
        this.configureBackground()
        this.configureClickAction()
        this.configureWidth()
    }

    //Configura o rótulo do botão
    private fun configureLabel() {
        binding.textViewText.text = label
        binding.textViewText.setTextColor(Color.parseColor(labelColor))
    }

    //Configura o ícone, se disponível
    private fun configureIcon() {
        if (icon != null && icon != 0) { // Verifica se o ícone é válido
            binding.imageViewIcon.apply {
                visibility = View.VISIBLE
                setImageResource(icon!!)
                setColorFilter(getIconColor())
            }
            binding.textViewText.updateLeftMargin(10f)
        } else {
            binding.imageViewIcon.visibility = View.GONE
            binding.textViewText.updateLeftMargin(0f)
        }
    }



    private fun configureLoading(){
        binding.loading.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(labelColor))
    }



    //Obtém a cor do ícone, com fallback para um tom mais claro do background
    private fun getIconColor(): Int {
        return if (iconColor == null) {
            Color.parseColor(backgroundColor?.let { lightenColor(it, 70.0) })
        } else {
            Color.parseColor(iconColor)
        }
    }

    //Configura a navegação por foco
    @SuppressLint("ResourceType")
    private fun configureFocusNavigation() {
        if (blockActions.isNotEmpty()) {
            binding.mainContainer.blockDPadActions(blockActions)
        }


        //Ajustar navegação
        if(nextFocusLeftId > 0) binding.mainContainer.nextFocusLeftId = nextFocusLeftId
        if(nextFocusRightId > 0) binding.mainContainer.nextFocusRightId = nextFocusRightId
        if(nextFocusUpId > 0) binding.mainContainer.nextFocusUpId = nextFocusUpId
        if(nextFocusDownId > 0) binding.mainContainer.nextFocusDownId = nextFocusDownId
    }

    //Configura o background e comportamento de foco
    private fun configureBackground() {
        binding.mainContainer.apply {
            //Estilo padrão
            styleBackground(backgroundColor = backgroundColor, radius = 18f)

            //Estilo baseado no foco
            setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    onFocus(binding.mainContainer.id)
                }

                val borderWidth = if (hasFocus) 3 else 0
                val borderColor = if (hasFocus) borderColorOnFocus else "#00000000"
                val bgColor = if (hasFocus) backgroundColor?.let { darkenColor(it, 10.0) } else backgroundColor

                styleBackground(bgColor, radius = 18f, borderWidth = borderWidth, borderColor = borderColor)
            }
        }
    }

    //Configura a ação de clique
    private fun configureClickAction() {
        binding.mainContainer.setOnClickListener() {
            val originalBackgroundColor = backgroundColor
            val lightenBackgroundColor = backgroundColor?.let { lightenColor(it, 5.toDouble()) }

            var borderWidth = 3
            val borderColor = borderColorOnFocus ?: ContextManager.getColorHex(0)

            // Simula o "click" visualmente clareando o fundo
            if (lightenBackgroundColor != null) {
                binding.mainContainer.styleBackground(
                    backgroundColor = lightenBackgroundColor,
                    radius = 18f,
                    borderWidth = borderWidth,
                    borderColor = borderColor
                )
            }

            handler.postDelayed({
                action()
                onClickListener?.invoke()


                if (originalBackgroundColor != null) {
                    binding.mainContainer.styleBackground(
                        backgroundColor = originalBackgroundColor,
                        radius = 18f,
                        borderWidth = 0,
                        borderColor = borderColor
                    )
                }
                binding.mainContainer.requestFocus()
            }, 100)
        }
    }


    fun setOnClick(listener: () -> Unit) {
        this.onClickListener = listener
    }


    //Configura a largura do componente
    private fun configureWidth() {
        if (width != 0f) {
            binding.mainContainer.setWidthInDp(width!!)
        } else {
            binding.mainContainer.layoutParams.width = context.dpToPx(DEFAULT_BUTTON_WIDTH_DP)
            //binding.mainContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }

    }


    //Atualiza a margem esquerda do texto
    private fun View.updateLeftMargin(marginDp: Float) {
        (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
            it.leftMargin = context.dpToPx(marginDp)
            layoutParams = it
        }
    }

    //Retorna o ID do componente
    fun getComponentId(): Int = binding.mainContainer.id

    //Atualiza o texto do botão
    fun setLabel(newLabel: String) {
        label = newLabel
        binding.textViewText.text = newLabel
    }

    //Atualiza a cor do rótulo
    fun setLabelColor(newColor: String) {
        labelColor = newColor
        binding.textViewText.setTextColor(Color.parseColor(newColor))
    }


    //Atualiza o ícone do botão
    fun setIcon(@DrawableRes newIcon: Int?) {
        icon = newIcon
        configureIcon()
    }


    //Atualiza a cor do ícone
    fun setIconColor(newColor: String?) {
        iconColor = newColor
        configureIcon()
    }

    //Atualiza a cor de fundo
    fun setBackgroundColor(newColor: String) {
        backgroundColor = newColor
        configureBackground()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setFocusability(status: Boolean){
        if(status) {
            binding.mainContainer.focusable = View.FOCUSABLE
        }
        else{
            binding.mainContainer.focusable = View.NOT_FOCUSABLE
        }
    }

    fun showLoading(show: Boolean){
        if (show){
            binding.mainContainer.isClickable = false

            val tempBlockedActions = mutableListOf(KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)

            binding.mainContainer.blockDPadActions(tempBlockedActions)


            binding.contentContainer.fadeOutAnimationNoRepeat(200){
                binding.contentContainer.visibility = View.INVISIBLE
                binding.loading.fadeInAnimationNoRepeat(200)
            }
        }
        else{
            if(blockActions.isNotEmpty()) {
                binding.mainContainer.blockDPadActions(blockActions)
            }
            else{
                //binding.mainContainer.resetDPadActions()
            }

            binding.loading.fadeOutAnimationNoRepeat(200){
                binding.loading.visibility = View.INVISIBLE
                binding.contentContainer.fadeInAnimationNoRepeat(200)

                binding.mainContainer.isClickable = true
            }
        }
    }


    override fun setNextFocusLeftId(id: Int) {
        binding.mainContainer.nextFocusLeftId = id
    }

    override fun setNextFocusRightId(id: Int) {
        binding.mainContainer.nextFocusRightId = id
    }

    override fun setNextFocusUpId(id: Int) {
        binding.mainContainer.nextFocusUpId = id
    }

    override fun setNextFocusDownId(id: Int) {
        binding.mainContainer.nextFocusDownId = id
    }

    companion object {
        private const val DEFAULT_BUTTON_WIDTH_DP = 352.818f
        private const val DEFAULT_BUTTON_HEIGHT_DP = 48f
    }
}