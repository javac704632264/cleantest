package com.ai.subscription.ui;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ai.subscription.R;
import com.doodlecamera.base.holder.BaseRecyclerViewHolder;

public class SubDetailTipItemViewHolder extends BaseRecyclerViewHolder<SubDetailTipInfo> {
    private ImageView mIvBg;
    private TextView mTvDes;

    public SubDetailTipItemViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
        mIvBg = itemView.findViewById(R.id.iv_top_icon);
        mTvDes = itemView.findViewById(R.id.tv_desc);

    }

    @Override
    public void onBindViewHolder(SubDetailTipInfo itemData) {
        super.onBindViewHolder(itemData);
        if (itemData != null) {
            mIvBg.setImageResource(itemData.mBgRes);
            mTvDes.setText(itemData.mTextRes);
        }
    }
}
