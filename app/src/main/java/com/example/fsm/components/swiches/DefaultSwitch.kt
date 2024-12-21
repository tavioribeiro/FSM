package com.example.fsm.components.swiches


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.example.fsm.R
import com.example.fsm.core.extentions.ContextManager
import com.example.fsm.core.extentions.dpToPx
import com.example.fsm.databinding.CLayoutDefaultSwitchBinding
import core.extensions.blockDPadActions
import core.extensions.setHeightInDp
import core.extensions.setWidthInDp
import core.extensions.styleBackground

class DefaultSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private var isChecked: Boolean = false,
    private var checkedColor: String? = null,
    private var uncheckedColor: String? = null,
    private var thumbColor: String? = null,
    private var blockActions: MutableList<Int> = mutableListOf(),
    private var nextFocusLeftId: Int = 0,
    private var nextFocusRightId: Int = 0,
    private var nextFocusUpId: Int = 0,
    private var nextFocusDownId: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: CLayoutDefaultSwitchBinding = CLayoutDefaultSwitchBinding.inflate(LayoutInflater.from(context), this, true)
    private var onCheckedChangeListener: ((isChecked: Boolean) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DefaultSwitch,
            0, 0
        ).apply {
            try {
                isChecked = getBoolean(R.styleable.DefaultSwitch_isChecked, false)
                checkedColor = getString(R.styleable.DefaultSwitch_checkedColor) ?: ContextManager.getColorHex(2) // Green
                uncheckedColor = getString(R.styleable.DefaultSwitch_uncheckedColor) ?: ContextManager.getColorHex(3) // Gray
                thumbColor = getString(R.styleable.DefaultSwitch_thumbColor) ?: ContextManager.getColorHex(5) // White
                nextFocusLeftId = getResourceId(R.styleable.DefaultSwitch_nextFocusLeft, 0)
                nextFocusRightId = getResourceId(R.styleable.DefaultSwitch_nextFocusRight, 0)
                nextFocusUpId = getResourceId(R.styleable.DefaultSwitch_nextFocusUp, 0)
                nextFocusDownId = getResourceId(R.styleable.DefaultSwitch_nextFocusDown, 0)
                val blockActionsString = getString(R.styleable.DefaultSwitch_blockActions)
                blockActionsString?.split(",")?.map { it.trim().toIntOrNull() }?.filterNotNull()?.let {
                    blockActions.addAll(it)
                }
            } finally {
                recycle()
            }
        }

        setupComponent()
    }

    private fun setupComponent() {
        configureFocusNavigation()
        configureSwitchAppearance()
        configureClickAction()
        updateSwitchState()
    }

    private fun configureFocusNavigation() {
        binding.mainContainer.apply {
            if (nextFocusLeftId != 0) nextFocusLeftId = this@DefaultSwitch.nextFocusLeftId
            if (nextFocusRightId != 0) nextFocusRightId = this@DefaultSwitch.nextFocusRightId
            if (nextFocusUpId != 0) nextFocusUpId = this@DefaultSwitch.nextFocusUpId
            if (nextFocusDownId != 0) nextFocusDownId = this@DefaultSwitch.nextFocusDownId
            if (blockActions.isNotEmpty()) blockDPadActions(blockActions)
        }
    }

    private fun configureSwitchAppearance() {
        binding.mainContainer.apply {
            setHeightInDp(28f)
            setWidthInDp(50f)
            styleBackground(backgroundColor = if (isChecked) checkedColor else uncheckedColor, radius = 50f)
        }

        binding.thumb.apply {
            setHeightInDp(24f)
            setWidthInDp(24f)
            styleBackground(backgroundColor = thumbColor, radius = 50f)
        }
    }

    private fun configureClickAction() {
        binding.mainContainer.setOnClickListener {
            isChecked = !isChecked
            updateSwitchState()
            onCheckedChangeListener?.invoke(isChecked)
            animateThumb()
        }
    }

    private fun updateSwitchState() {
        val trackColor = if (isChecked) checkedColor else uncheckedColor
        binding.mainContainer.styleBackground(backgroundColor = trackColor, radius = 30f)
    }

    private fun animateThumb() {
        var targetTranslationX = if (isChecked) {
                binding.mainContainer.layoutParams.width.toFloat() - binding.thumb.layoutParams.width.toFloat()
            }
            else{
                0f
            }

        binding.thumb.animate()
            .translationX(targetTranslationX)
            .setDuration(150)
            .start()
    }

    fun setOnCheckedChangeListener(listener: (isChecked: Boolean) -> Unit) {
        this.onCheckedChangeListener = listener
    }

    // Métodos para atualizar o estado do switch programaticamente
    fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            updateSwitchState()
            animateThumb()
        }
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    // Métodos para mudar as cores dinamicamente
    fun setCheckedColor(@ColorInt color: Int) {
        checkedColor = String.format("#%06X", 0xFFFFFF and color)
        updateSwitchState()
    }

    fun setUncheckedColor(@ColorInt color: Int) {
        uncheckedColor = String.format("#%06X", 0xFFFFFF and color)
        updateSwitchState()
    }

    fun setThumbColor(@ColorInt color: Int) {
        thumbColor = String.format("#%06X", 0xFFFFFF and color)
        binding.thumb.styleBackground(backgroundColor = thumbColor, radius = 50f)
    }

    companion object {
        private const val DEFAULT_SWITCH_WIDTH_DP = 60f
        private const val DEFAULT_SWITCH_HEIGHT_DP = 30f
    }
}