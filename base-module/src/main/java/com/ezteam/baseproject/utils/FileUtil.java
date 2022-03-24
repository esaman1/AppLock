package com.ezteam.baseproject.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;

public class FileUtil {

    public static String getFileName(String path) {
        if (path == null) {
            return "not found";
        }
        int index = path.lastIndexOf('/');
        if (index == -1) {
            return "not found";
        } else {
            return path.substring(path.lastIndexOf('/'));
        }
    }

    public static String getFileExt(String path) {
        return path.substring(path.lastIndexOf('.'));
    }


    public static void scanFile(final Context context, String pathFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(pathFile));
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            MediaScannerConnection.scanFile(context, new String[]{pathFile}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } else {
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse(pathFile)));
        }
    }


    public static String getFileSize(double paramDouble) {
        double d1 = paramDouble / 1024.0D;
        double d2 = d1 / 1024.0D;
        double d3 = d2 / 1024.0D;
        if (paramDouble < 1024.0D) {
            return paramDouble + " Bytes";
        }
        if (d1 < 1024.0D) {
            return new BigDecimal(d1).setScale(2, 4).toString() + " KB";
        }
        if (d2 < 1024.0D) {
            return new BigDecimal(d2).setScale(2, 4).toString() + " MB";
        }
        return new BigDecimal(d3).setScale(2, 4).toString() + " GB";
    }

    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
