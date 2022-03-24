package com.ezteam.applocker.utils

import android.content.Context
import android.view.Window
import com.ezteam.applocker.dialog.DialogHiding

object DialogLoadingUtils {
    private var dialogHiding: DialogHiding? = null

     fun showDialogHiding(context: Context, isShowing :Boolean) {
        if(isShowing) {
            dialogHiding = DialogHiding(context)
            dialogHiding?.setCancelable(false)
            dialogHiding?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogHiding?.show()
        }else {
            dialogHiding?.dismiss()
        }
    }

}