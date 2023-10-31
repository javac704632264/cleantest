package com.ai.subscription.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.ai.subscription.R;
import com.ai.subscription.util.UserAgreementUtil;
import com.doodlecamera.widget.dialog.base.BaseActionDialogFragment;

public class SubLoadingDialogFragment extends BaseActionDialogFragment {
    private static final String TAG = "SubLoadingDialogFragment";

    public static final String EXTRA_MSG = "msg";

    private String mMessage;
    private TextView mMessageView;

    public static SubLoadingDialogFragment showProgressDialog(FragmentActivity activity, String tag) {
        return showProgressDialog(activity, tag, null);
    }

    public static SubLoadingDialogFragment showProgressDialog(FragmentActivity activity, String tag, String msg) {
        Bundle progressArgs = new Bundle();
        progressArgs.putString(SubLoadingDialogFragment.EXTRA_MSG, msg);
        SubLoadingDialogFragment progressDialogFragment = new SubLoadingDialogFragment();
        progressDialogFragment.setArguments(progressArgs);
        progressDialogFragment.show(activity.getSupportFragmentManager(), tag);
        return progressDialogFragment;
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCouldCancel(true);
        Bundle args = getArguments();
        mMessage = null;
        if (args != null) {
            mMessage = args.getString(EXTRA_MSG);
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_loading_dialog_fragment, container, false);
        mMessageView = view.findViewById(R.id.progress_text);
        if (!TextUtils.isEmpty(mMessage)) {
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(mMessage);
        } else {
            mMessageView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideNavigationBar();
    }

    private void hideNavigationBar() {
        if (getDialog() == null)
            return;
        UserAgreementUtil.hideNavigationBar(getDialog().getWindow());
    }
}
