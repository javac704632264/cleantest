package com.ai.subscription.ui;

import static com.ai.subscription.component.SubPortal.PORTAL_QUIT_INTERCEPT;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.ai.subscription.R;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.hepler.SubscribeBean;
import com.ai.subscription.hepler.SubscribeHelper;
import com.ai.subscription.purchase.BuyCallback;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.util.SubscribeStats;
import com.ai.subscription.util.UserAgreementUtil;
import com.doodlecamera.tools.core.utils.ui.SafeToast;
import com.doodlecamera.widget.dialog.base.BaseActionDialogFragment;


public class SubGiveUpDialogFragment extends BaseActionDialogFragment {

    private String mProductId;

    private TextView sub_giveup_discount_tv;
    private TextView sub_retain_buy_tv;
    private View subBg;
    private TextView buyPriceView;
    private TextView sub_description;

    public static final String GIVEUP_PORTAL = "giveup_retain_buy";

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_back_retain, container, false);
        sub_giveup_discount_tv = view.findViewById(R.id.sub_giveup_discount_tv);
        sub_retain_buy_tv = view.findViewById(R.id.sub_buy_tv);
        subBg = view.findViewById(R.id.sub_view_bg);
        buyPriceView = view.findViewById(R.id.sub_buy_price_tv);
        sub_description = view.findViewById(R.id.sub_description);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProductId = ConfigStyle.getFirstProductID(PORTAL_QUIT_INTERCEPT);

        sub_giveup_discount_tv.setText(getString(R.string.sub_giveup_discount, ConfigStyle.getDiscount(PORTAL_QUIT_INTERCEPT, mProductId)));

        setBuyText();

        UserAgreementUtil.setUserAgreements(getActivity(), sub_description, "---", Color.parseColor("#A2A4BD"));

        ((SubscriptionActivity) getActivity()).getSubscribeHelper().getLiveData().observe(getViewLifecycleOwner(), new Observer<SubscribeBean>() {
            @Override
            public void onChanged(SubscribeBean bean) {

                UserAgreementUtil.setUserAgreements(getActivity(), sub_description, getPriceText(bean, mProductId), Color.parseColor("#A2A4BD"));
            }
        });

        subBg.setOnClickListener(v -> {
            SubscribeStats.clickStats(((SubscriptionActivity) getActivity()).getPortal(), mProductId, true, false);

            PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
            if (manager == null)
                return;

            if (!manager.icConnectionSuccess()) {
                manager.reConnect();
                SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
                return;
            }

            IAPManager.getInstance().buy(getActivity(), mProductId, GIVEUP_PORTAL, new BuyCallback() {
                @Override
                public void onBuySuccess(String productId, Purchase purchase) {
                    SubscribeStats.statsPaySucc(((SubscriptionActivity) getActivity()).getPortal(), true, productId, purchase.getOrderId(), purchase.getOriginalJson(), false);

                    dismiss();
                }

                @Override
                public void onBuyFail(String productId, int errorCode, String reason) {
                    SubscribeStats.statsPayFail(((SubscriptionActivity) getActivity()).getPortal(), true, productId, reason, errorCode, false);
                    dismiss();
                }
            });
        });

        view.findViewById(R.id.sub_giveup_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().finish();
            }
        });

        String portal = ((SubscriptionActivity) getActivity()).getPortal();
        SubscribeStats.showStatsForQuitIntercept(portal, mProductId);
    }

    protected String getPriceText(SubscribeBean bean, String productID) {
        if (bean == null)
            return "---";

        String value = bean.getPriceDollarPeriodWrapper(productID);
        if (TextUtils.isEmpty(value))
            value = "---";
        return value;
    }

    private void setBuyText() {
        int trialDay = ConfigStyle.getTrialDay(PORTAL_QUIT_INTERCEPT, mProductId);
        if (trialDay > 0) {
            sub_retain_buy_tv.setText(getString(R.string.sub_trail_buy, trialDay + ""));
            setBuyPrice(mProductId);
        } else {
            sub_retain_buy_tv.setText(getString(R.string.sub_trail_direct_buy));
            buyPriceView.setVisibility(View.GONE);
        }
    }


    private void setBuyPrice(final String productID) {
        SubscribeBean bean = getSubscribeHelper().getLiveData().getValue();
        setBuyPriceValue(bean, productID);

        getSubscribeHelper().getLiveData().observe(getViewLifecycleOwner(), bean1 -> setBuyPriceValue(bean1, productID));
    }

    private void setBuyPriceValue(SubscribeBean bean, String id) {
        if (bean == null || TextUtils.isEmpty(bean.getPriceDollarPeriodWrapper(id))) {
            buyPriceView.setVisibility(View.GONE);
            return;
        }

        buyPriceView.setVisibility(View.VISIBLE);
        String price = bean.getPriceDollarPeriodWrapper(id);
        buyPriceView.setText(getString(R.string.sub_buy_price, price));
    }


    private SubscribeHelper getSubscribeHelper() {
        return ((SubscriptionActivity) requireActivity()).getSubscribeHelper();
    }
}
