package com.google.android.gms.ads.ez;


import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.ez.observer.MyObserver;
import com.google.android.gms.ads.ez.observer.MySubject;

public class BannerAd extends RelativeLayout {
    private final String TAG = "BannerAd";
    private AdView fbBanner;
    private com.google.android.gms.ads.AdView admobBanner;
    private Context mContext;
    private boolean isReloadFB = false;
    private AdManagerAdView adxBanner;

    private MyObserver mObserver = new MyObserver() {
        @Override
        public void update(String message) {
            if (message.equals(IAPUtils.KEY_PURCHASE_SUCCESS)) {
                LogUtils.logString(IAPUtils.class, "BannerAd user purchase observer -> remove ads");
                setVisibility(GONE);
            }
        }
    };


    public BannerAd(Context context) {
        super(context);


        mContext = context;
        initViews();
    }

    public BannerAd(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        initViews();
    }

    private void initViews() {

        MySubject.getInstance().attach(mObserver);
        if (IAPUtils.getInstance().isPremium( )) {
            LogUtils.logString(IAPUtils.class, "BannerAd user purchase init -> remove ads");
            setVisibility(GONE);
            return;
        }


        loadAdmob();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        setLayoutParams(layoutParams);
    }

    private void loadFacebookBanner() {


        fbBanner = new AdView(mContext, AdUnit.getFacebookBannerId(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);


        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                loadAdx();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                removeAllViews();
                addView(fbBanner);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        fbBanner.loadAd(fbBanner.buildLoadAdConfig().withAdListener(adListener).build());
    }

    private void loadAdmob() {

        admobBanner = new com.google.android.gms.ads.AdView(mContext);
        admobBanner.setAdSize(getAdSize());
        admobBanner.setAdUnitId(AdUnit.getAdmobBannerId());
        AdRequest adRequest = new AdRequest.Builder().build();
        admobBanner.loadAd(adRequest);
        admobBanner.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "onAdFailedToLoad: ");
                loadFacebookBanner();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "onAdLoaded: ");
                removeAllViews();
                addView(admobBanner);
            }
        });


    }


    private void loadAdx() {

        adxBanner = new AdManagerAdView(mContext);

        adxBanner.setAdSizes(getAdSize());

        adxBanner.setAdUnitId(AdUnit.getAdxBannerId());

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adxBanner.loadAd(adRequest);

        adxBanner.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e(TAG, "Adx onAdLoaded: ");
                removeAllViews();
                addView(adxBanner);
                setVisibility(View.VISIBLE);
            }


            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.e(TAG, "Adx onAdFailedToLoad: ");
                setVisibility(GONE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }


            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        try {
//            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = mContext.getResources().getDisplayMetrics();
//            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;

            int adWidth = (int) (widthPixels / density);
            Log.e(TAG, "getAdSize: " + adWidth + "  " + outMetrics.widthPixels);
            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mContext, adWidth);
        } catch (Exception e) {
            Log.e(TAG, "getAdSize: ", e);
            e.printStackTrace();
        }
        return com.google.android.gms.ads.AdSize.BANNER;
    }

}