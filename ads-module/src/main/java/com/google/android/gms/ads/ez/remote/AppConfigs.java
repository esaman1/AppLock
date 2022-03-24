package com.google.android.gms.ads.ez.remote;

import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.ez.LogUtils;
import com.google.android.gms.ads.ez.R;
import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class AppConfigs {

    private Application mApplication;
    private static AppConfigs _instance;
    private FirebaseRemoteConfig config;
    // thời gian hết hiệu lực của config
    private static final long CONFIG_EXPIRE_SECOND = 12 * 3600;

    private AppConfigs(Application application) {
        mApplication = application;
        setConfig();
    }

    public FirebaseRemoteConfig getConfig() {
        return this.config;
    }

    public void setConfig() {
        LogUtils.logString(AppConfigs.class, "setConfig");
        if (FirebaseApp.getApps(mApplication.getApplicationContext()).isEmpty()) {
            return;
        }
        LogUtils.logString(AppConfigs.class, "xx");
        //Get instance của remote config
        config = FirebaseRemoteConfig.getInstance();


        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        //Setting chế độ debug
        config.setConfigSettingsAsync(configSettings);

        // set default
        config.setDefaultsAsync(R.xml.remote_ads);

        //Vì chúng ta đang trong debug mode nên cần config được fetch và active ngay lập tức sau khi thay đổi trên console
        long expireTime = AdUnit.TEST ? 1 : CONFIG_EXPIRE_SECOND;
        //Mỗi lần khởi chạy app sẽ fetch config về và nếu thành công thì sẽ active config vừa lấy về
        config.fetch(expireTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LogUtils.logString(AppConfigs.class, "Fuckkk");
                        if (task.isSuccessful()) {
                            config.fetchAndActivate();

                        }
                    }
                });


    }

    public static AppConfigs getInstance(Application application) {
        if (_instance == null) {
            _instance = new AppConfigs(application);
        }
        return _instance;
    }

    public static String getString(String key) {
        if (_instance == null) {
            return "";
        }
        return _instance.getConfig().getString(key);
    }

    public static Long getLong(String key) {
        if (_instance == null) {
            return 0L;
        }
        return _instance.getConfig().getLong(key);
    }

    public static Integer getInt(String key) {
        if (_instance == null) {
            return 0;
        }
        return Integer.valueOf(_instance.getConfig().getString(key));
    }

    public static boolean getBoolean(String key) {
        if (_instance == null) {
            return false;
        }
        return _instance.getConfig().getBoolean(key);
    }
}