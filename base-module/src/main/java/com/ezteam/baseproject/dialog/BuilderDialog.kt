package com.ezteam.baseproject.dialog

import android.content.Context
import java.util.*

abstract class BuilderDialog(var context: Context) {
    var title: String? = null
    var message: String? = null
    var positiveButton: String? = null
    var negativeButton: String? = null
    var cancelable = true
    var canOnTouchOutside = true
    var positiveButtonListener: ((BaseDialog<*, *>?, HashMap<String?, Any?>) -> Unit)? = null
    var negativeButtonListener: ((BaseDialog<*, *>) -> Unit)? = null
    var dismissDialogListener: (() -> Unit)? = null

    fun setTitle(title: String?): BuilderDialog {
        this.title = title
        return this
    }

    fun setMessage(message: String?): BuilderDialog {
        this.message = message
        return this
    }

    fun setCancelable(cancelable: Boolean): BuilderDialog {
        this.cancelable = cancelable
        return this
    }

    fun setCanOnTouchOutside(canOnTouchOutside: Boolean): BuilderDialog {
        this.canOnTouchOutside = canOnTouchOutside
        return this
    }

    fun onSetPositiveButton(
        positiveButton: String?,
        positiveButtonListener: ((BaseDialog<*, *>?, HashMap<String?, Any?>) -> Unit)? = null
    ): BuilderDialog {
        this.positiveButton = positiveButton
        this.positiveButtonListener = positiveButtonListener
        return this
    }

    fun onSetNegativeButton(
        negativeButton: String?, negativeButtonListener: ((BaseDialog<*, *>) -> Unit)? = null
    ): BuilderDialog {
        this.negativeButton = negativeButton
        this.negativeButtonListener = negativeButtonListener
        return this
    }

    fun onDismissListener(dismissDialogListener: () -> Unit): BuilderDialog {
        this.dismissDialogListener = dismissDialogListener
        return this
    }

    abstract fun build(): BaseDialog<*, *>
}