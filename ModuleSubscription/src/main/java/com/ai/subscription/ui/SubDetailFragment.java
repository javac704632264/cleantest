package com.ai.subscription.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.billingclient.api.Purchase;
import com.ai.subscription.R;
import com.ai.subscription.component.ISubFragment;
import com.ai.subscription.component.SubPortal;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.hepler.SubscribeBean;
import com.ai.subscription.hepler.SubscribeHelper;
import com.ai.subscription.purchase.BuyCallback;
import com.ai.subscription.purchase.Client;
import com.ai.subscription.purchase.IQueryResult;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.stats.AdjustCollector;
import com.ai.subscription.stats.FirebaseCollector;
import com.ai.subscription.util.PeroidUtils;
import com.ai.subscription.util.SubscribeStats;
import com.ai.subscription.util.UserAgreementUtil;
import com.doodlecamera.base.core.thread.TaskHelper;
import com.doodlecamera.base.core.utils.lang.ObjectStore;
import com.doodlecamera.tools.core.utils.ui.SafeToast;
import com.doodlecamera.tools.core.utils.ui.ViewUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SubDetailFragment extends Fragment implements View.OnClickListener, ISubFragment {
    private static final String TAG = "SubDetailFragment";

    private String mSelectProductId;

    private TextView userAgreementView;
    private TextView restoreView;
    private ImageView mBackImgView;
    private View titleLayout;

    private TextView buyView;
    private TextView buyPriceView;

    private TextView titleView;
    private RecyclerView mTipRecyclerView;
    private DotsView mDotsView;

    private SubLoadingDialogFragment mLoadingDialogFragment;

    private final int[] mPriceIds = new int[]{R.id.sub_price_1, R.id.sub_price_2, R.id.sub_price_3};
    private final List<View> mPriceViews = new ArrayList<>();
    private final List<String> mProductIds = new ArrayList<>();
    private boolean needShowLoading;
    private String mStatsPortal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    boolean viewCreated;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subs_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();

        if (needShowLoading) {
            showLoadingDialog();
        }
        viewCreated = true;
        SubscribeStats.showStats(mStatsPortal, getProductIdsStr());
        AdjustCollector.onEvent(AdjustCollector.SUB_SHOW_TOKEN);
        Bundle params = new Bundle();
        FirebaseCollector.onEvent(FirebaseCollector.SUB_SHOW, params);
        SubscribeStats.showStatsSupport();
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mStatsPortal = arguments.getString("stats_portal");
        }

        List<String> stringSet = ConfigStyle.getTop2ProductID(SubPortal.PORTAL_MAIN);
        mProductIds.addAll(stringSet);
        styleUpdate();
        updateData();
        noGPServiceHint();
    }

    private void initView(View view) {
        for (int priceId : mPriceIds) {
            View priceView = view.findViewById(priceId);
            priceView.setOnClickListener(this);
            mPriceViews.add(priceView);
        }
        titleView = view.findViewById(R.id.sub_title);
        buyView = view.findViewById(R.id.sub_buy_tv);
        View buyLayout = view.findViewById(R.id.sub_buy_layout);
        buyLayout.setOnClickListener(this);

        buyPriceView = view.findViewById(R.id.sub_buy_price_tv);

        restoreView = view.findViewById(R.id.sub_restore);
        restoreView.setOnClickListener(this);

        mBackImgView = view.findViewById(R.id.sub_back_iv);
        mBackImgView.setOnClickListener(this);

        userAgreementView = view.findViewById(R.id.sub_description);
        titleLayout = view.findViewById(R.id.sub_title_layout);

        mTipRecyclerView = view.findViewById(R.id.recycler_view);
        mDotsView = view.findViewById(R.id.dots_view);
        mTipRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        SubDetailTipAdapter adapter = new SubDetailTipAdapter();
        mTipRecyclerView.setAdapter(adapter);
        mTipInfos  = new ArrayList<>();
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_1, R.string.sub_tip_1));
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_2, R.string.sub_tip_2));
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_3, R.string.sub_tip_3));
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_4, R.string.sub_tip_4));
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_5, R.string.sub_tip_5));
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_6, R.string.sub_tip_6));
        mTipInfos.add(new SubDetailTipInfo(R.drawable.vip_detail_tip_bg_7, R.string.sub_tip_7));
        adapter.updateDataAndNotify(mTipInfos,true);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mTipRecyclerView);
    }

    private List<SubDetailTipInfo> mTipInfos;
    private int mCurrentPos = 0;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mTipRecyclerView != null) {
                    TaskHelper.exec(new TaskHelper.UITask() {
                        @Override
                        public void callback(Exception e) {
                            mCurrentPos++;
                            if (mCurrentPos > mTipInfos.size()) {
                                mCurrentPos = 0;
                            }
                            if (mCurrentPos == 0) {
                                mTipRecyclerView.scrollToPosition(0);
                            } else {
                                mTipRecyclerView.smoothScrollToPosition(mCurrentPos);
                            }
                            mDotsView.setSelect(mCurrentPos);
                        }
                    });
                }
            }
        };
        mTimer.schedule(mTimerTask, 0,2000L);
    }

    private void stopTimer() {
        if (mTimer != null)
            mTimer.cancel();
        if (mTimerTask != null)
            mTimerTask.cancel();
    }

    public void setNeedShowLoading(boolean needShowLoading) {
        this.needShowLoading = needShowLoading;

        if (viewCreated && needShowLoading) showLoadingDialog();
    }

    private void showLoadingDialog() {
        SubscribeStats.showStatsEnterLoading("sub_loading");
        mLoadingDialogFragment = SubLoadingDialogFragment.showProgressDialog(requireActivity(), "subloading", ObjectStore.getContext().getResources().getString(R.string.sub_loading));
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialogFragment != null) {
            mLoadingDialogFragment.dismiss();
            mLoadingDialogFragment = null;
        }
    }

    @Override
    public void showRetryFetchDeshowtailView(boolean show) {
    }

    private void styleUpdate() {
        mSelectProductId = ConfigStyle.getDefaultProductID(SubPortal.PORTAL_MAIN);
        updateCurrentCardView(getSelectIdIndex());
    }

    private int getSelectIdIndex() {
        if (mProductIds.isEmpty() || TextUtils.isEmpty(mSelectProductId)) {
            return -1;
        }
        return mProductIds.indexOf(mSelectProductId);
    }

    private void updateCurrentCardView(int index) {
        String productId = index < 0 || mProductIds.isEmpty() ? "" : mProductIds.get(index);
        for (int i = 0; i < mPriceViews.size(); i++) {
            if (index == i) {
                View priceView = mPriceViews.get(i);
                View cardView = priceView.findViewById(R.id.sub_price_card);
                cardView.setBackgroundResource(R.drawable.sub_price_card_bg);

                TextView dateView = priceView.findViewById(R.id.sub_price_date);
                dateView.setTextColor(getResources().getColor(R.color.color_191b20));

                TextView priceTextView = priceView.findViewById(R.id.sub_price_text);
                priceTextView.setTextColor(getResources().getColor(R.color.color_333333));

                TextView periodView = priceView.findViewById(R.id.sub_price_period);
                periodView.setTextColor(getResources().getColor(R.color.color_8b8da1));

                TextView savedView = priceView.findViewById(R.id.sub_price_saved);
                savedView.setTextColor(getResources().getColor(R.color.color_f2c38b));

                View bottomView = priceView.findViewById(R.id.sub_card_bottom);
                bottomView.setBackgroundResource(R.drawable.sub_price_card_bg_2);
                setBuyText(productId);
            }
        }
    }

    private void updateLastCardView(int index) {
        String productId = index < 0 || mProductIds.isEmpty() ? "" : mProductIds.get(index);
        for (int i = 0; i < mPriceViews.size(); i++) {
            if (index == i) {
                View priceView = mPriceViews.get(i);
                View cardView = priceView.findViewById(R.id.sub_price_card);
                cardView.setBackgroundResource(R.drawable.sub_price_card_bg_normal);

                TextView dateView = priceView.findViewById(R.id.sub_price_date);
                dateView.setTextColor(getResources().getColor(R.color.color_8b8da1));

                TextView priceTextView = priceView.findViewById(R.id.sub_price_text);
                priceTextView.setTextColor(getResources().getColor(R.color.color_8b8da1));

                TextView periodView = priceView.findViewById(R.id.sub_price_period);
                periodView.setTextColor(getResources().getColor(R.color.color_8b8da1));

                TextView savedView = priceView.findViewById(R.id.sub_price_saved);
                savedView.setTextColor(getResources().getColor(R.color.color_f2c38b));

                View bottomView = priceView.findViewById(R.id.sub_card_bottom);
                bottomView.setBackgroundResource(0);
                setBuyText(productId);
            }
        }
    }

    private void setBuyText(String productID) {
        int trialDay = ConfigStyle.getTrialDay(SubPortal.PORTAL_MAIN, productID);
        if (trialDay > 0) {
            buyView.setText(getString(R.string.sub_trail_buy, trialDay + ""));
            setBuyPrice(productID);
            titleView.setText(getString(R.string.sub_trail_buy, trialDay + ""));
        } else {
            buyView.setText(getString(R.string.sub_trail_direct_buy));
            buyPriceView.setVisibility(View.GONE);
            titleView.setText(getString(R.string.sub_title_new));
        }
    }

    private SubscribeHelper getSubscribeHelper() {
        return ((SubscriptionActivity) requireActivity()).getSubscribeHelper();
    }

    private void updateData() {
        SubscribeBean bean = getSubscribeHelper().getLiveData().getValue();
        setTextValue(bean);

        UserAgreementUtil.setUserAgreements(getActivity(), userAgreementView, getPriceText(bean, mSelectProductId), getResources().getColor(R.color.color_a2a4bd));

        getSubscribeHelper().getLiveData().observe(getViewLifecycleOwner(), bean1 -> {
            setTextValue(bean1);
            changeUserAgreement(bean1);
        });
    }

    private void changeUserAgreement(SubscribeBean bean) {
        if (bean == null) return;

        UserAgreementUtil.setUserAgreements(getActivity(), userAgreementView, getPriceText(bean, mSelectProductId), getResources().getColor(R.color.color_a2a4bd));
    }

    private String getPriceText(SubscribeBean bean, String productID) {
        if (bean == null) return "---";

        String value = bean.getPriceDollarPeriodWrapper(productID);
        if (TextUtils.isEmpty(value)) value = "---";
        return value;
    }

    @SuppressLint("SetTextI18n")
    private void setTextValue(SubscribeBean bean) {
        for (View priceView : mPriceViews) {
            String productId = mProductIds.get(mPriceViews.indexOf(priceView));
            TextView date = priceView.findViewById(R.id.sub_price_date);
            TextView priceTextView = priceView.findViewById(R.id.sub_price_text);
            TextView discountView = priceView.findViewById(R.id.sub_price_discount);
            TextView periodView = priceView.findViewById(R.id.sub_price_period);
            TextView savedView = priceView.findViewById(R.id.sub_price_saved);

            String billingPeriod = bean == null ? "" : bean.getPeriodModeWrapper(productId);

            String monthPeriod = PeroidUtils.getPeriod(billingPeriod);
            if (TextUtils.isEmpty(monthPeriod)) {
                monthPeriod = PeroidUtils.getPeriodForId(productId);
            }
            date.setText(monthPeriod);

            String price = bean == null ? "--" : bean.getPriceDollarWrapper(productId);
            priceTextView.setText(TextUtils.isEmpty(price) ? "--" : price);

            String discount = ConfigStyle.getDiscount(SubPortal.PORTAL_MAIN, productId);
            discountView.setVisibility(existDiscount(discount) ? View.VISIBLE : View.INVISIBLE);
            discountView.setText(discount + " " + ObjectStore.getContext().getResources().getString(R.string.sub_off).toUpperCase(Locale.ROOT));

            periodView.setVisibility(TextUtils.equals(productId, Client.PRODUCT_ID_MONTH) ? View.INVISIBLE : View.VISIBLE);
            periodView.setText(getPricePeriod(billingPeriod, productId, bean));
            String promotionInformation = ConfigStyle.getPromotionInformation(SubPortal.PORTAL_MAIN, productId);
            savedView.setVisibility(TextUtils.isEmpty(promotionInformation) ? View.INVISIBLE : View.VISIBLE);
            savedView.setText(promotionInformation);
        }
    }

    private String getPricePeriod(String billingPeriod, String productId, SubscribeBean bean) {
        String monthText = ObjectStore.getContext().getResources().getString(R.string.sub_month_periodd);
        String defaultText = "--/" + monthText;

        if (bean == null) {
            return defaultText;
        }

        double priceAmount = bean.getPriceAmount(productId);
        if (priceAmount <= 0) {
            return defaultText;
        }

        int months = PeroidUtils.getProductMonths(billingPeriod);
        if (months <= 0) {
            return defaultText;
        }

        double priceMonth = priceAmount / months;
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        Currency priceCurrency = bean.getPriceCurrency(productId);
        numberFormat.setCurrency(priceCurrency);
        String formatText = numberFormat.format(priceMonth);
        if (TextUtils.isEmpty(formatText)) {
            return defaultText;
        }

        return formatText + "/" + monthText;
    }

    private boolean existDiscount(String discount) {
        return !TextUtils.isEmpty(discount) && !"0".equals(discount);
    }

    @Override
    public void onClick(View v) {
        for (View priceView : mPriceViews) {
            if (priceView.getId() == v.getId()) {
                updateLastCardView(getSelectIdIndex());
                int index = mPriceViews.indexOf(priceView);
                mSelectProductId = mProductIds.get(index);
                updateCurrentCardView(index);
                changeUserAgreement(getSubscribeHelper().getLiveData().getValue());
                return;
            }
        }

        if (v.getId() == R.id.sub_buy_layout) {
            if (ViewUtils.isClickTooFrequently(v, 1500)) {
                return;
            }

            handleBuyClick();
            return;
        }

        if (v.getId() == R.id.sub_restore) {
            handleRestoreClick();
            return;
        }

        if (v.getId() == R.id.sub_back_iv) {
            ((SubscriptionActivity) getActivity()).onBackPressedEx();
        }
    }

    private void handleBuyClick() {
        PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
        if (manager == null)
            return;

        if (!manager.icConnectionSuccess()) {
            manager.reConnect();
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
            return;
        }

        SubscribeStats.clickStats(mStatsPortal, mSelectProductId, false, false);

        IAPManager.getInstance().buy((FragmentActivity) getContext(), mSelectProductId, "multi_btn", new BuyCallback() {
            @Override
            public void onBuySuccess(String productId, Purchase purchase) {
                SubscribeStats.statsPaySucc(mStatsPortal, false, productId, purchase.getOrderId(), purchase.getOriginalJson(), false);
            }

            @Override
            public void onBuyFail(String productId, int errorCode, String reason) {
                SubscribeStats.statsPayFail(mStatsPortal, false, productId, reason, errorCode, false);
            }
        });
    }

    private void handleRestoreClick() {
        PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
        if (manager == null) return;

        if (!manager.icConnectionSuccess()) {
            manager.reConnect();
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
            return;
        }
        IAPManager.getInstance().updateVipState(new IQueryResult() {
            @Override
            public void querySuccess() {
                IAPManager.getInstance().showSuccessDialog(getActivity());
                SafeToast.showToast(R.string.sub_restore_status_success, Toast.LENGTH_SHORT);
            }

            @Override
            public void queryFail() {
                SafeToast.showToast(R.string.sub_restore_status_fail, Toast.LENGTH_SHORT);
            }
        });
    }

    private void noGPServiceHint() {
        PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
        if (manager != null && !manager.icConnectionSuccess()) {
            manager.reConnect();
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public String getProductIdsStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mProductIds.size(); i++) {
            sb.append(mProductIds.get(i));
            if (i < mProductIds.size() - 1) {
                sb.append(",");

            }
        }
        return sb.toString();
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


    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }
}
