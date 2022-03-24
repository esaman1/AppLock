package com.ezteam.baseproject.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.ezteam.baseproject.R;

public class HelperFunctions {
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static void setStatusBarHomeTransparent(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            }
            if (Build.VERSION.SDK_INT >= 19) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            //make fully Android Transparent Status bar
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                window.setStatusBarColor(activity.getResources().getColor(R.color.tokenBlack50));
            }
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static int getHeightStatusBar(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static DividerItemDecoration simpleDividerItemDecoration(Context context,
                                                                    int orientation,
                                                                    float dpSize,
                                                                    @ColorInt int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, context.getResources().getDisplayMetrics());
        gradientDrawable.setSize(size, size);
        gradientDrawable.setColor(color);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, orientation);
        dividerItemDecoration.setDrawable(gradientDrawable);

        return dividerItemDecoration;
    }
}
