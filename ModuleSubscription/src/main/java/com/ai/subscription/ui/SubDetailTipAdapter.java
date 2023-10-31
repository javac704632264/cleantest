package com.ai.subscription.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ai.subscription.R;
import com.doodlecamera.base.adapter.BaseRecyclerViewAdapter;
import com.doodlecamera.base.holder.BaseRecyclerViewHolder;

public class SubDetailTipAdapter extends BaseRecyclerViewAdapter<SubDetailTipInfo, BaseRecyclerViewHolder<SubDetailTipInfo>> {

    @NonNull
    @Override
    public BaseRecyclerViewHolder<SubDetailTipInfo> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubDetailTipItemViewHolder(parent, R.layout.subs_detail_tip_item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder<SubDetailTipInfo> holder, int position) {
        holder.onBindViewHolder(getItem(position));
    }
}
