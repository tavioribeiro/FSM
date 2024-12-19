package core.extensions

import android.app.Activity
import android.widget.Toast

fun Activity.toast(message: Any) {
    runOnUiThread {
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show()
    }
}
