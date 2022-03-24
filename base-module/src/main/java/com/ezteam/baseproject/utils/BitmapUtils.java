package com.ezteam.baseproject.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class BitmapUtils {

    public static Bitmap mergeBitmaps(Bitmap bitmapFirst, Bitmap bitmapSecond) {
        Bitmap combined = Bitmap.createBitmap(bitmapFirst.getWidth(), bitmapFirst.getHeight(),
                bitmapFirst.getConfig());
        Canvas canvas = new Canvas(combined);
        canvas.drawBitmap(bitmapFirst, new Matrix(), null);
        canvas.drawBitmap(bitmapSecond, new Matrix(), null);
        return combined;
    }


    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
