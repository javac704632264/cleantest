package com.ai.subscription.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.ai.subscription.R;
import com.ai.subscription.cache.IFetchDetail;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.hepler.SubscribeHelper;
import com.ai.subscription.manager.SubManager;
import com.ai.subscription.purchase.BuyCallback;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.ui.dialog.GiveUpDialog;
import com.ai.subscription.util.SubscribeSettings;
import com.ai.subscription.util.SubscribeStats;
import com.android.billingclient.api.Purchase;
import com.doodlecamera.base.activity.BaseActivity;
import com.doodlecamera.base.core.utils.lang.ObjectStore;
import com.doodlecamera.net.utils.NetworkUtils;
import com.doodlecamera.tools.core.utils.ui.SafeToast;


public class SubscriptionActivity extends BaseActivity {
    private SubscribeHelper mSubscribeHelper;
    private String mStatsPortal;

    private Fragment subFragment;

    private long mStartTime;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public IFetchDetail mIFetchDetail = new IFetchDetail() {

        private static final int DELAY_TIME_SHOW = 3 * 1000;

        @Override
        public void fetchStart() {
            if (!NetworkUtils.isConnected(ObjectStore.getContext()))
                return;

            PurchaseManager.log("fetchStart() callback");

            mStartTime = System.currentTimeMillis();
//            if (subFragment instanceof SubDetailFragment) {
//                ((SubDetailFragment) subFragment).setNeedShowLoading(true);
//            }
        }

        @Override
        public void fetchResult(final boolean suc) {

            if (!NetworkUtils.isConnected(ObjectStore.getContext()))
                return;

            PurchaseManager.log("fetchResult() callback  suc = " + suc);

            if (System.currentTimeMillis() - mStartTime <= DELAY_TIME_SHOW) {
                mHandler.postDelayed(() -> showRetry(suc), DELAY_TIME_SHOW);
            } else {
                showRetry(suc);
            }
        }

        private void showRetry(boolean suc) {
            SubscribeStats.showStatsEnterLoading(suc + "");
//            if (subFragment instanceof SubDetailFragment) {
//                SubDetailFragment subDetailFragment = ((SubDetailFragment) subFragment);
//                subDetailFragment.setNeedShowLoading(false);
//                subDetailFragment.dismissLoadingDialog();
//                subDetailFragment.showRetryFetchDeshowtailView(!suc);
//            }
        }

    };

    @Override
    public String getFeatureId() {
        return "SubscriptionActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

//        hideTitleBar();
        mSubscribeHelper = new SubscribeHelper(mIFetchDetail);
        findViewById(R.id.sub_top_right_iv).bringToFront();
        mStatsPortal = getIntent().getStringExtra("portal");
        boolean vip = IAPManager.getInstance().isVIP();
        subFragment = new com.ai.subscription.ui.fragment.SubDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stats_portal", mStatsPortal);
        subFragment.setArguments(bundle);
        showFragment(subFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public SubscribeHelper getSubscribeHelper() {
        return mSubscribeHelper;
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.subs_container, fragment).commitAllowingStateLoss();
    }

    public String getPortal() {
        return mStatsPortal;
    }

    @Override
    public void onBackPressedEx() {
        if (!IAPManager.getInstance().isVIP()) {
            if (canShowQuitIntercept()) {
                SubscribeSettings.setQuitInterceptTime(System.currentTimeMillis());
                PurchaseManager.log("onBackPressedEx()  show()    mGiveUpDialogFragment ");
                new GiveUpDialog.Builder(this)
                        .setMoney(SubManager.Companion.getInstance().getExitSubPrice())
                        .setPercent(SubManager.Companion.getInstance().getExitSavePercent())
                        .addOnEventListener(new GiveUpDialog.OnEventListener() {
                            @Override
                            public void onCancel() {
                                finish();
                            }

                            @Override
                            public void onContinue() {
                                //跳转谷歌订阅支付页
                                PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
                                if (manager == null)
                                    return;

                                if (!manager.icConnectionSuccess()) {
                                    manager.reConnect();
                                    SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
                                    return;
                                }

                                IAPManager.getInstance().buy(SubscriptionActivity.this, SubManager.Companion.getInstance().getSubId(), "giveup_portal", new BuyCallback() {
                                    @Override
                                    public void onBuySuccess(String productId, Purchase purchase) {
                                        SubscribeStats.statsPaySucc(getPortal(), true, productId, purchase.getOrderId(), purchase.getOriginalJson(), false);

                                    }

                                    @Override
                                    public void onBuyFail(String productId, int errorCode, String reason) {
                                        SubscribeStats.statsPayFail(getPortal(), true, productId, reason, errorCode, false);

                                    }
                                });
                            }
                        }).show();
                return;
            }
        }

        super.onBackPressedEx();
    }

    public boolean canShowQuitIntercept() {
        if (!SubManager.Companion.getInstance().getExitSwitch()) {
            return false;
        }
        if (SubscribeSettings.getQuitInterceptTime() == -1)
            return true;

        if (System.currentTimeMillis() - SubscribeSettings.getQuitInterceptTime() >= 24 * 60 * 60 * 1000)
            return true;

        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected int getPrimaryDarkColor() {
        return R.color.transparent;
    }

    @Override
    protected int getPrimaryColor() {
        return R.color.transparent;
    }

}
