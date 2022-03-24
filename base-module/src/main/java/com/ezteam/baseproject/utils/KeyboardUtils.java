package com.ezteam.baseproject.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtils {

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    public static void hideSoftKeyboardToggleSoft(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }

    public static void showSoftKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }

    public static void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            imm.showSoftInput(view, 0);
        }
    }

    public static void autoHideClickView(final View view, final Activity context) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(context);
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                autoHideClickView(innerView, context);
            }
        }
    }

    public static void autoHideClickView(final View view, final Activity context, View... viewException) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(context);
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                if (viewException != null) {
                    for (View aViewException : viewException) {
                        if (aViewException.getId() != innerView.getId()) {
                            autoHideClickView(innerView, context);
                        }
                    }
                } else {
                    autoHideClickView(innerView, context);
                }
            }
        }
    }
}
