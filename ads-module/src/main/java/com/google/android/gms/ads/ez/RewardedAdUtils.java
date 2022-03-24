package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.ez.utils.StateOption;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;

public class RewardedAdUtils {

    private static RewardedAdUtils INSTANCE;


    public static RewardedAdUtils getInstance(Activity context) {
        if (INSTANCE == null) {
            INSTANCE = new RewardedAdUtils(context);
        }
        INSTANCE.mActivity = context;
        return INSTANCE;
    }


    public RewardedAdUtils(Activity mContext) {
        this.mActivity = mContext;
        loadAd();
    }

    private RewardedAd mRewardedAd;
    public Activity mActivity;
    private AdFactoryListener adListener;
    private StateOption stateOption = StateOption.getInstance();

    public RewardedAdUtils setAdListener(AdFactoryListener adListener) {
        this.adListener = adListener;
        return this;
    }

    public void loadAd() {

        if (stateOption.isLoading()) {
            return;
        }
        LogUtils.logString(RewardedAdUtils.class, "Load Ad ");
        stateOption.setOnLoading();
        AdRequest adRequest = new AdRequest.Builder().build();
        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("292FA13BA1218092001E4E2A0EC83C31")).build();
        MobileAds.setRequestConfiguration(configuration);
        RewardedAd.load(mActivity, AdUnit.getAdmobRewardedId(),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        stateOption.setOnFailed();
                        LogUtils.logString(RewardedAdUtils.class, "Error " + loadAdError.getMessage());
                        mRewardedAd = null;
                        if (adListener != null) {
                            adListener.onError();
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        stateOption.setOnLoaded();
                        mRewardedAd = rewardedAd;
                        LogUtils.logString(RewardedAdUtils.class, "Ad was loaded.");

                        if (adListener != null) {
                            adListener.onLoaded();
                        }
                    }
                });


    }

    public boolean canShowAds() {
        if (mRewardedAd != null && mActivity instanceof Activity && stateOption.isLoaded()) {
            return true;
        }
        return false;
    }

    public void showAds() {
        LogUtils.logString(RewardedAdUtils.class, "xxxx" + mRewardedAd + "   " + (mActivity instanceof Activity) + "   " + stateOption.isLoaded());
        if (mRewardedAd != null && mActivity instanceof Activity && stateOption.isLoaded()) {

            if (adListener != null) {
                adListener.onLoaded();
            }

            Activity activityContext = (Activity) mActivity;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    LogUtils.logString(RewardedAdUtils.class, "The user earned the reward.");

                    if (adListener != null) {
                        adListener.onEarnedReward();
                    }

                }
            });
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    stateOption.setShowAd();
                    // Called when ad is shown.
                    LogUtils.logString(RewardedAdUtils.class, "Ad was shown.");
                    mRewardedAd = null;
                    stateOption.setShowAd();

                    if (adListener != null) {
                        adListener.onDisplay();
                        adListener.onRewardVideoStart();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    LogUtils.logString(RewardedAdUtils.class, "Ad failed to show.");
                    if (adListener != null) {
                        adListener.onDisplayFaild();
                    }
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    stateOption.setDismisAd();
                    loadAd();
                    // Called when ad is dismissed.
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    LogUtils.logString(RewardedAdUtils.class, "Ad was dismissed.");
                    if (adListener != null) {
                        adListener.onClosed();
                    }
                }
            });
        } else {
            if (adListener != null) {
                adListener.onError();
            }
            LogUtils.logString(RewardedAdUtils.class, "The rewarded ad wasn't ready yet. " + (mActivity instanceof Activity) + "   " + mRewardedAd);
        }
    }

}
