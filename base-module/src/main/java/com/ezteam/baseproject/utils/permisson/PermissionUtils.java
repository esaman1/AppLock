package com.ezteam.baseproject.utils.permisson;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static final int MY_PERMISSIONS_REQUEST = 111;

    public static void requestRuntimePermission(Activity activity, String... permissions) {
        if(!checkPermissonAccept(activity, permissions)){
            ActivityCompat.requestPermissions(activity, permissions, MY_PERMISSIONS_REQUEST);
        }
    }

    public static boolean checkPermissonAccept(Context context, String... permissons) {
        for (String permissonName : permissons) {
            if (ContextCompat.checkSelfPermission(context, permissonName) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
