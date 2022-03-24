package com.google.android.gms.ads.ez;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.ads.ez.analytics.FirebaseAnalTool;

public class InstallReferrerReceiver extends BroadcastReceiver {

    private static final String TAG_SAVE_REFERRER = "tag_save_referrer";
    String referrer = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null) {
            if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
                FirebaseAnalTool.getInstance(context).trackEvent("Ref1", "onReceive");
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    referrer = extras.getString("referrer");
                    Log.e("InstallReferrerReceiver", "onReceive1: " + referrer);
                    FirebaseAnalTool.getInstance(context).trackEvent("Ref1", referrer);
                    /*
                    If you want split username and code use below code...
                    for ex referrer="username12345678890"
                    if(referrer!=null)
                    {
                        String[] referrerParts = referrer.split("(?<=\\D)(?=\\d)");
                        String strName = referrerParts[0];
                        String strCode = referrerParts[1];

                        Log.e("Receiver Referral Code", "===>" + strName);
                        Log.e("Receiver Referral Name", "===>" + strCode);

                        PrefUtils.putPrefString(context, PrefUtils.PRF_REFERRER_CODE, strName);
                        PrefUtils.putPrefString(context, PrefUtils.PRF_REFERRER_NAME, strCode);
                    }*/
                }
            }
        }
    }

    public static void receiverInstall(Context context) {
        InstallReferrerClient referrerClient;

        referrerClient = InstallReferrerClient.newBuilder(context).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        ReferrerDetails response = null;
                        try {
                            String referrerUrl = null;
                            response = referrerClient.getInstallReferrer();
                            referrerUrl = response.getInstallReferrer();
                            if (!SharedPreferencesUtils.getTagBoolean(context, TAG_SAVE_REFERRER, false)) {
                                SharedPreferencesUtils.setTagBoolean(context, TAG_SAVE_REFERRER, true);
                                FirebaseAnalTool.getInstance(context).trackEvent("Ref2", referrerUrl);
                                Log.e("InstallReferrerReceiver", "onInstallReferrerSetupFinished: " );
                            }
                            Log.e("InstallReferrerReceiver", "onReceive2: " + referrerUrl);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        // Connection established.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }


}