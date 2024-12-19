package com.example.fsm.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.example.fsm.R
import com.example.fsm.core.extentions.ContextManager
import com.example.fsm.core.extentions.dpToPx
import com.example.fsm.core.utils.log
import com.example.fsm.databinding.LayoutDefaultButtonBinding
import core.extensions.blockDPadActions
import core.extensions.darkenColor
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
    private var width: Float? = null,
    private var default_width: Boolean ?= null,
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

    private val binding: LayoutDefaultButtonBinding = LayoutDefaultButtonBinding.inflate(LayoutInflater.from(context), this, true)
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
                label = getString(R.styleable.DefaultButton_label) ?: ""
                width = width ?: getFloat(R.styleable.DefaultButton_width, 0f)
                default_width = default_width ?: getBoolean(R.styleable.DefaultButton_default_width, true)
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
        this.configureLabel()
        this.configureIcon()
        this.configureFocusNavigation()
        this.configureBackground()
        this.configureClickAction()
        this.configureWidth()
    }

    //Configura o rótulo do botão
    private fun configureLabel() {

        log("label $label")
        if(label == ""){
            binding.imageViewIcon.apply {
                visibility = View.GONE
            }
        }
        else{
            binding.textViewText.text = label
            binding.textViewText.setTextColor(Color.parseColor(labelColor))
        }
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
        binding.mainContainer.apply {
            if (nextFocusLeftId > 0) nextFocusLeftId = this@DefaultButton.nextFocusLeftId
            if (nextFocusRightId > 0) nextFocusRightId = this@DefaultButton.nextFocusRightId
            if (nextFocusUpId > 0) nextFocusUpId = this@DefaultButton.nextFocusUpId
            if (nextFocusDownId > 0) nextFocusDownId = this@DefaultButton.nextFocusDownId
            if (blockActions.isNotEmpty()) blockDPadActions(blockActions)
        }
    }

    //Configura o background e comportamento de foco
    private fun configureBackground() {
        binding.mainContainer.apply {
            //Estilo padrão
            styleBackground(backgroundColor = backgroundColor, radius = 24f)

            //Estilo baseado no foco
            setOnFocusChangeListener { _, hasFocus ->
                val borderWidth = if (hasFocus) 3 else 0
                val borderColor = if (hasFocus) borderColorOnFocus else "#00000000"
                val bgColor = if (hasFocus) backgroundColor?.let { darkenColor(it, 10.0) } else backgroundColor

                styleBackground(bgColor, radius = 24f, borderWidth = borderWidth, borderColor = borderColor)
            }
        }
    }

    //Configura a ação de clique
    // Atualiza a configuração de clique
    private fun configureClickAction() {
        binding.mainContainer.setOnClickListener {
            // Verifica se o botão tem foco
            val hasFocus = binding.mainContainer.isFocused
            val originalBackgroundColor = backgroundColor
            val lightenBackgroundColor = backgroundColor?.let { lightenColor(it, 5.toDouble()) }

            val borderWidth = if (hasFocus) 3 else 0
            val borderColor = if (hasFocus) borderColorOnFocus ?: ContextManager.getColorHex(0) else "#00000000"

            // Simula o "click" visualmente clareando o fundo
            if (lightenBackgroundColor != null) {
                binding.mainContainer.styleBackground(
                    backgroundColor = lightenBackgroundColor,
                    radius = 24f,
                    borderWidth = borderWidth,
                    borderColor = borderColor
                )
            }

            handler.postDelayed({
                action()
                onClickListener?.invoke()

                // Restaura o background após o clique
                if (originalBackgroundColor != null) {
                    binding.mainContainer.styleBackground(
                        backgroundColor = originalBackgroundColor,
                        radius = 24f,
                        borderWidth = borderWidth,
                        borderColor = borderColor
                    )
                }
            }, 100)
        }
    }



    fun setOnClickListener(listener: () -> Unit) {
        this.onClickListener = listener
    }

    //Configura a largura do componente
    private fun configureWidth() {
        if(label == "") {
            binding.mainContainer.setWidthInDp(60f)
            binding.mainContainer.setHeightInDp(60f)

            binding.textViewText.visibility = View.GONE

            binding.imageViewIcon.setWidthInDp(30f)
            binding.imageViewIcon.setHeightInDp(30f)
        }
        else{
            if (default_width == true) {
                binding.mainContainer.layoutParams.width = context.dpToPx(DEFAULT_BUTTON_WIDTH_DP)
            } else {
                if (width != null) {
                    if (width != 0f) {
                        binding.mainContainer.setWidthInDp(width!!)
                    } else {
                        binding.mainContainer.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                } else {
                    binding.mainContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
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
    fun setIconResource(@DrawableRes newIcon: Int?) {
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


    companion object {
        private const val DEFAULT_BUTTON_WIDTH_DP = 140.33333f
        private const val DEFAULT_BUTTON_HEIGHT_DP = 38f
    }
}