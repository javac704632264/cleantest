package com.ai.subscription.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.ai.subscription.R;
import com.doodlecamera.widget.dialog.base.BaseActionDialogFragment;

public class SubSuccessDialogFragment extends BaseActionDialogFragment {
    private static final String TAG = "SubSuccessDialogFragment";

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_success, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() == null)
            return;

        view.findViewById(R.id.sub_restart_view).setOnClickListener(v -> {
            handleBackClick();
        });

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (getActivity() != null && getDialog() != null && getDialog().isShowing() && !isRemoving()) {
                    handleBackClick();
                }
            }
            return false;
        });
    }

    private void handleBackClick() {
//        SRouter.getInstance().build(HomeRouterHub.UI.MAIN).navigation(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
}
