package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.Apk;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class ApkAdapter extends BaseQuickAdapter<Apk, ApkAdapter.ViewHolder> {


    public ApkAdapter(@Nullable List<Apk> data) {
        super(R.layout.item_apk, data);
    }

    @Override
    protected void convert(ViewHolder helper, Apk item) {
        helper.mTvName.setText(item.getName());
        String size = String.format("%.2f", item.getSize() / 1024f / 1024f) + "M" ;
        helper.mTvSize.setText(size);
        helper.mIvIcon.setImageDrawable(item.getPic());
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_size)
        TextView mTvSize;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
