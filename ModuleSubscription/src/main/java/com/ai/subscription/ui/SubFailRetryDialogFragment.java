package com.ai.subscription.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.billingclient.api.Purchase;
import com.ai.subscription.R;
import com.ai.subscription.component.SubPortal;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.hepler.SubscribeBean;
import com.ai.subscription.hepler.SubscribeHelper;
import com.ai.subscription.purchase.BuyCallback;
import com.ai.subscription.purchase.Client;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.util.SubscribeStats;
import com.doodlecamera.tools.core.utils.ui.SafeToast;
import com.doodlecamera.widget.dialog.base.BaseActionDialogFragment;

public class SubFailRetryDialogFragment extends BaseActionDialogFragment {

    private static final String TAG = "SubFailRetryDialogFragment";

    public static final String RETRY_PID = "retry_pid";

    private String mProductId;

    private TextView retryView;

    private TextView buyTextView;

    private TextView cancelView;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent);

        Bundle args = getArguments();
        if (args == null)
            return;

        mProductId = args.getString(RETRY_PID);
        PurchaseManager.log(" SubFailRetryDialogFragment onCreate()  mProductId = " + mProductId);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retryView = view.findViewById(R.id.fail_retry_btn);
        buyTextView = view.findViewById(R.id.sub_buy_price_tv);
        cancelView = view.findViewById(R.id.fail_close_btn);

        if (TextUtils.isEmpty(mProductId))
            mProductId = Client.PRODUCT_ID_YEAR;

        final String portal = ((SubscriptionActivity) getActivity()).getPortal();
        retryView.setOnClickListener(v -> {
            handleRetryClick(portal);
        });
        setBuyText();
        cancelView.setOnClickListener(v -> dismiss());

        SubscribeStats.showStatsForRetry(portal, "", mProductId);
    }

    private void setBuyText() {
        int trialDay = ConfigStyle.getTrialDay(SubPortal.PORTAL_MAIN, mProductId);
        if (trialDay > 0) {
            retryView.setText(getString(R.string.sub_trail_buy, trialDay + ""));
            SubscribeBean bean = getSubscribeHelper().getLiveData().getValue();
            if (bean == null || TextUtils.isEmpty(bean.getPriceDollarPeriodWrapper(mProductId))) {
                buyTextView.setVisibility(View.GONE);
            } else {
                buyTextView.setVisibility(View.VISIBLE);
                String price = bean.getPriceDollarPeriodWrapper(mProductId);
                buyTextView.setText(getString(R.string.sub_buy_price, price));
            }
        } else {
            retryView.setText(getString(R.string.sub_trail_direct_buy));
            buyTextView.setVisibility(View.GONE);
        }
    }

    private SubscribeHelper getSubscribeHelper() {
        return ((SubscriptionActivity) requireActivity()).getSubscribeHelper();
    }

    private void handleRetryClick(String portal) {
        SubscribeStats.clickStats(portal, mProductId, false, true);

        PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
        if (manager == null)
            return;

        if (!manager.icConnectionSuccess()) {
            manager.reConnect();
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
            return;
        }

        IAPManager.getInstance().buy(getActivity(), mProductId, "fail_retry", new BuyCallback() {
            @Override
            public void onBuySuccess(String productId, Purchase purchase) {
                SubscribeStats.statsPaySucc(portal, false, productId, purchase.getOrderId(), purchase.getOriginalJson(), true);
            }

            @Override
            public void onBuyFail(String productId, int errorCode, String reason) {
                SubscribeStats.statsPayFail(portal, false, productId, reason, errorCode, true);
            }
        });

        dismiss();
    }
}
