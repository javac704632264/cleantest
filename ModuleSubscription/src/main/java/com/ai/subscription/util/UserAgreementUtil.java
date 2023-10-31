package com.ai.subscription.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.ai.subscription.R;
import com.ai.subscription.config.BasicsKeys;
import com.ai.subscription.config.ConfigStyle;
import com.doodlecamera.base.core.utils.lang.ObjectStore;
import com.doodlecamera.hybrid.HybridConfig;
import com.doodlecamera.hybrid.HybridManager;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/26
 * Time   :   11:23 上午
 */
public class UserAgreementUtil {

    public static void setUserAgreements(FragmentActivity activity, TextView userAgreementView, String priceProid, int agrremntColor) {
        String policy = ObjectStore.getContext().getResources().getString(R.string.string_privacy_policy);
        String userAgreement = ObjectStore.getContext().getResources().getString(R.string.string_user_agreement);
        String contentStr = ObjectStore.getContext().getResources().getString(R.string.string_select_vip, userAgreement, policy);

        int policyIndex = contentStr.indexOf(policy);
        int agreementIndex = contentStr.indexOf(userAgreement);

//        contentStr = contentStr.replace("%1$s","---").replace("%2$s","---");
        SpannableString spannableString = new SpannableString(contentStr);
        spannableString.setSpan(new ClickSpanOpen(activity, getPrivacyUrl(),agrremntColor), policyIndex, policyIndex + policy.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickSpanOpen(activity, getPrivacyUrl(),agrremntColor), agreementIndex, agreementIndex + userAgreement.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        userAgreementView.setHighlightColor(Color.TRANSPARENT);
        userAgreementView.setText(spannableString);

        userAgreementView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setUserAgreements(FragmentActivity activity, TextView userAgreementView,int agrremntColor){
        String policy = ObjectStore.getContext().getResources().getString(R.string.string_privacy_policy);
        String userAgreement = ObjectStore.getContext().getResources().getString(R.string.string_user_agreement);
        String contentStr = ObjectStore.getContext().getResources().getString(R.string.string_select_vip, userAgreement, policy);

        int policyIndex = contentStr.indexOf(policy);
        int agreementIndex = contentStr.indexOf(userAgreement);

//        contentStr = contentStr.replace("%1$s","---").replace("%2$s","---");
        SpannableString spannableString = new SpannableString(contentStr);
        spannableString.setSpan(new ClickSpanOpen(activity, getPrivacyUrl(),agrremntColor), policyIndex, policyIndex + policy.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickSpanOpen(activity, getUserAgreementUrl(),agrremntColor), agreementIndex, agreementIndex + userAgreement.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        userAgreementView.setHighlightColor(Color.TRANSPARENT);
        userAgreementView.setText(spannableString);

        userAgreementView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static String getPrivacyUrl() {
        return ConfigStyle.getSubPrivacyUrl();
    }

    private static String getUserAgreementUrl(){
        return BasicsKeys.USER_AGREEMENT;
    }


    private static class ClickSpanOpen extends URLSpan {
        private FragmentActivity context;
        private int color = Color.parseColor("#A2A4BD");

        public ClickSpanOpen(FragmentActivity context, String url,int color) {
            super(url);
            this.context = context;
            this.color = color;
        }

        @Override
        public void onClick(View widget) {
            try {
                if (TextUtils.isEmpty(getURL()))
                    return;

                //todo testused
                // IAPManager.getInstance().showRetryBuyDialog(context, Client.PRODUCT_ID_MONTH);

                HybridConfig.ActivityConfig config = new HybridConfig.ActivityConfig();
                config.setPortal("sub_user_agreement");
                config.setStyle(HybridConfig.STYLE_NO_TITLE);
                config.setUrl(getURL());
                HybridManager.startRemoteActivity(context, config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
//            ds.setColor(Color.parseColor("#A2A4BD"));
            ds.setColor(color);
            //ds.setColor(Color.WHITE);
            ds.setStrokeWidth(ds.getStrokeWidth() * 2);
            ds.setStyle(Paint.Style.FILL_AND_STROKE);
            ds.setUnderlineText(true);
        }
    }

    public static void  hideNavigationBar(Window window) {
        if (window == null)
            return;

        int flag =  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        window.getDecorView().setSystemUiVisibility(flag);
    }
}
