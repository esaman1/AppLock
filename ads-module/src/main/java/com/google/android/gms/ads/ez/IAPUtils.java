package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.ez.observer.MySubject;

import java.util.ArrayList;
import java.util.List;

public class IAPUtils {
    private List<SkuDetails> skuDetailsList = new ArrayList<>();
    private Application application;
    private static IAPUtils INSTANCE;
    public static final String KEY_PREMIUM_ONE_MONTH = "one_month";
    public static final String KEY_PREMIUM_SIX_MONTHS = "six_months";
    public static final String KEY_PREMIUM_ONE_YEAR = "one_year";
    public static final String KEY_PURCHASE_SUCCESS = "purchase_success";
    private BillingClient billingClient;


    public boolean isPremium() {
        if (isSubscriptions(KEY_PREMIUM_ONE_MONTH) || isSubscriptions(KEY_PREMIUM_SIX_MONTHS) || isSubscriptions(KEY_PREMIUM_ONE_YEAR)) {
            return true;
        }
        return false;
    }


    public static IAPUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IAPUtils();
        }
        return INSTANCE;
    }


    public void init(Application application) {
        LogUtils.logString(IAPUtils.class, "init");
        this.application = application;
        billingClient = BillingClient.newBuilder(application)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    LogUtils.logString(IAPUtils.class, "onBillingSetupFinished ok");
                    getAllSubcriptions();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


    }


    // get all subcription
    public void getAllSubcriptions() {
        LogUtils.logString(IAPUtils.class, "getAllSubcriptions ");
        List<String> skuListToQuery = new ArrayList<>();
        skuListToQuery.add(KEY_PREMIUM_ONE_MONTH);
        skuListToQuery.add(KEY_PREMIUM_SIX_MONTHS);
        skuListToQuery.add(KEY_PREMIUM_ONE_YEAR);

        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuListToQuery)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {

                if (list != null && !list.isEmpty()) {

                    skuDetailsList.addAll(list);


                    if (skuDetailsList.isEmpty()) {
                        return;
                    }


                    for (SkuDetails details : list) {
                        LogUtils.logString(IAPUtils.class, "getAllSubcriptions " + details.toString());
                    }
                }
            }
        });

    }

    public SkuDetails getSubcriptionById(String id) {
        LogUtils.logString(IAPUtils.class, "getSubcriptionById ");
        if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
            for (SkuDetails details : skuDetailsList) {
                if (details.getSku().equals(id)) {
                    return details;
                }
            }
        }
        return null;

//        getSku = productId
//        getPrice = price
    }

    public boolean isPurchase(String id) {
        if (billingClient == null || !billingClient.isReady()) {
            return false;
        }
        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
        if (purchases == null) {
            LogUtils.logString(IAPUtils.class, "Chua mua");
            return false;
        } else {
            LogUtils.logString(IAPUtils.class, "Da mua " + purchases.size());
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSubscriptions(String id) {
        if (billingClient == null || !billingClient.isReady()) {
            return false;
        }
        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
        if (purchases == null) {
            LogUtils.logString(IAPUtils.class, "Chua mua");
            return false;
        } else {
            LogUtils.logString(IAPUtils.class, "Da mua " + purchases.size());
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void callPurchase(Activity activity, String id) {
        List<String> skuListToQuery = new ArrayList<>();
        skuListToQuery.add(id);

        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuListToQuery)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (list != null && !list.isEmpty()) {
                    skuDetailsList.addAll(list);


                    if (skuDetailsList.isEmpty()) {
                        return;
                    }
                    SkuDetails skuDetails = skuDetailsList.get(0);

                    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();

                    logResponse(responseCode);
                }
            }
        });

    }

    public void callSubcriptions(Activity activity, String id) {

//        if(true){
//            MySubject.getInstance().notifyChange(KEY_PURCHASE_SUCCESS);
//            return;
//        }
        List<String> skuListToQuery = new ArrayList<>();
        skuListToQuery.add(id);

        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuListToQuery)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (list != null && !list.isEmpty()) {
                    skuDetailsList.addAll(list);

                    if (skuDetailsList.isEmpty()) {
                        return;
                    }

                    SkuDetails skuDetails = null;
                    for (SkuDetails details : list) {
                        if (details.getSku().equals(id)) {
                            skuDetails = details;
                        }
                    }

                    if (skuDetails == null) {
                        return;
                    }

                    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();

                    logResponse(responseCode);
                }
            }
        });

    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (purchases == null) {
                return;
            }

            logResponse(billingResult.getResponseCode());

            LogUtils.logString(IAPUtils.class, "onPurchasesUpdated ");
            for (Purchase purchase : purchases) {
                LogUtils.logString(IAPUtils.class, "onPurchasesUpdated " + purchase.toString());
                LogUtils.logString(IAPUtils.class, "onPurchasesUpdated " + purchase.getSku());
                // mua hang thanh cong
                handlePurchase(purchase);
                MySubject.getInstance().notifyChange(KEY_PURCHASE_SUCCESS);

            }

        }
    };

    public void handlePurchase(Purchase purchase) {
        handleConsumableProduct(purchase);
        handleNonConsumableProduct(purchase);
    }

    public void handleConsumableProduct(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        billingClient.consumeAsync(consumeParams, (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == OK) {
                // Handle the success of the consume operation.
            }
        });
    }

    public void handleNonConsumableProduct(Purchase purchase) {
        if (purchase.getPurchaseState() == purchase.getPurchaseState()) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    //Handle acknowledge result
                });
            }
        }
    }


    private void logResponse(int responseCode) {
        switch (responseCode) {
            case SERVICE_TIMEOUT:
                LogUtils.logString(IAPUtils.class, "SERVICE_TIMEOUT");
                break;
            case FEATURE_NOT_SUPPORTED:
                LogUtils.logString(IAPUtils.class, "FEATURE_NOT_SUPPORTED");
                break;
            case SERVICE_DISCONNECTED:
                LogUtils.logString(IAPUtils.class, "SERVICE_DISCONNECTED");
                break;
            case OK:
                LogUtils.logString(IAPUtils.class, "OK");
                break;
            case USER_CANCELED:
                LogUtils.logString(IAPUtils.class, "USER_CANCELED");
                break;
            case SERVICE_UNAVAILABLE:
                LogUtils.logString(IAPUtils.class, "SERVICE_UNAVAILABLE");
                break;
            case BILLING_UNAVAILABLE:
                LogUtils.logString(IAPUtils.class, "BILLING_UNAVAILABLE");
                break;
            case ITEM_UNAVAILABLE:
                LogUtils.logString(IAPUtils.class, "ITEM_UNAVAILABLE");
                break;
            case DEVELOPER_ERROR:
                LogUtils.logString(IAPUtils.class, "DEVELOPER_ERROR");
                break;
            case ERROR:
                LogUtils.logString(IAPUtils.class, "ERROR");
                break;
            case ITEM_ALREADY_OWNED:
                LogUtils.logString(IAPUtils.class, "ITEM_ALREADY_OWNED");
                break;
            case ITEM_NOT_OWNED:
                LogUtils.logString(IAPUtils.class, "ITEM_NOT_OWNED");
                break;
        }
    }

    final int SERVICE_TIMEOUT = -3;
    final int FEATURE_NOT_SUPPORTED = -2;
    final int SERVICE_DISCONNECTED = -1;
    final int OK = 0;
    final int USER_CANCELED = 1;
    final int SERVICE_UNAVAILABLE = 2;
    final int BILLING_UNAVAILABLE = 3;
    final int ITEM_UNAVAILABLE = 4;
    final int DEVELOPER_ERROR = 5;
    final int ERROR = 6;
    final int ITEM_ALREADY_OWNED = 7;
    final int ITEM_NOT_OWNED = 8;
}