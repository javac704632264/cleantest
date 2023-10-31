package com.ai.subscription.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.subscription.R;
import com.ai.subscription.component.ISubFragment;
import com.ai.subscription.component.SubPortal;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.hepler.SubscribeBean;
import com.ai.subscription.hepler.SubscribeHelper;
import com.ai.subscription.purchase.IQueryResult;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.util.SubscribeSettings;
import com.ai.subscription.util.UserAgreementUtil;
import com.doodlecamera.base.core.thread.TaskHelper;
import com.doodlecamera.base.core.utils.lang.ObjectStore;
import com.doodlecamera.tools.core.utils.ui.SafeToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/8
 * Time   :   6:01 下午
 */
public class SubPurchasedFragment extends Fragment implements ISubFragment {
    private static final String TAG = "SubPurchasedFragment";
    private TextView restoreView;

    private RecyclerView mTipRecyclerView;
    private DotsView mDotsView;
    private TextView agreementView;
    private final List<String> mProductIds = new ArrayList<>();

    private SubLoadingDialogFragment mLoadingDialogFragment;

    private void showLoadingDialog() {
        mLoadingDialogFragment = SubLoadingDialogFragment.showProgressDialog(requireActivity(), "subloading", ObjectStore.getContext().getResources().getString(R.string.sub_loading));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subs_purchased_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initView(view);

        List<String> stringSet = ConfigStyle.getTop2ProductID(SubPortal.PORTAL_MAIN);
        mProductIds.addAll(stringSet);
    }

    private void initView(View view) {
        agreementView = view.findViewById(R.id.sub_description);
        view.findViewById(R.id.sub_back_iv).setOnClickListener(v -> {
            handleBackClick();
        });

        view.findViewById(R.id.sub_connect_tv).setOnClickListener(v -> {
            handleConnectClick();
        });

        restoreView = view.findViewById(R.id.sub_restore);
        restoreView.setOnClickListener(v -> handleRestoreClick());
        getSubscribeHelper().getLiveData().observe(getViewLifecycleOwner(), this::changeUserAgreement);
        UserAgreementUtil.setUserAgreements(getActivity(), agreementView, SubscribeSettings.getProductPrice(), Color.parseColor("#A2A4BD"));
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


    private void handleBackClick() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    private void handleConnectClick() {
//        SRouter.getInstance()
//                .build(HomeRouterHub.UI.MAIN)
//                .navigation(getContext());
    }

    private String getPriceText(SubscribeBean bean) {
        if (bean == null) return "---";
        String value = SubscribeSettings.getProductPrice();
        if (TextUtils.isEmpty(value)) value = "---";
        return value;
    }


    private SubscribeHelper getSubscribeHelper() {
        return ((SubscriptionActivity) requireActivity()).getSubscribeHelper();
    }

    private void changeUserAgreement(SubscribeBean bean) {
        if (agreementView != null) {
            UserAgreementUtil.setUserAgreements(getActivity(), agreementView, getPriceText(bean), getResources().getColor(R.color.color_a2a4bd));
        }
    }

    @Override
    public void setNeedShowLoading(boolean show) {

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

    private void handleRestoreClick() {
        PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
        if (manager == null) return;

        if (!manager.icConnectionSuccess()) {
            manager.reConnect();
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT);
            return;
        }
        showLoadingDialog();
        IAPManager.getInstance().updateVipState(new IQueryResult() {
            @Override
            public void querySuccess() {
                dismissLoadingDialog();
                IAPManager.getInstance().showSuccessDialog(getActivity());
                SafeToast.showToast(R.string.sub_restore_status_success, Toast.LENGTH_SHORT);
            }

            @Override
            public void queryFail() {
                dismissLoadingDialog();
                SafeToast.showToast(R.string.sub_restore_status_fail, Toast.LENGTH_SHORT);
            }
        });
    }
}
