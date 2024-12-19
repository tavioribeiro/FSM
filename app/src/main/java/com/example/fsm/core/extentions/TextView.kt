package core.extensions

import android.graphics.Color
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.setTextAnimation(text: String, duration: Long = 100, completion: (() -> Unit)? = null) {
    fadeOutAnimation(duration) {
        this.text = text
        fadeInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}


fun TextView.setTextHexColor(hexColor: String) {
    this.setTextColor(Color.parseColor(hexColor))
}

fun TextView.addUnderline() {
    val spannableString = SpannableString(this.text.toString())
    spannableString.setSpan(UnderlineSpan(), 0, this.text.toString().length, 0)

    this.text = spannableString
}

fun TextView.removeUnderline() {
    val spannableString = SpannableString(this.text)
    val underlines = spannableString.getSpans(0, spannableString.length, UnderlineSpan::class.java)
    for (underline in underlines) {
        spannableString.removeSpan(underline)
    }
    this.text = spannableString
}



fun TextView.addEllipsis(maxLength: Int) {
    if (text.length > maxLength) {
        text = text.substring(0, maxLength - 3) + "..."
    }
}
