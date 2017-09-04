package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.MediaEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class MediaAdapter extends BaseQuickAdapter<MediaEntity, MediaAdapter.ViewHolder> {


    public MediaAdapter(@Nullable List<MediaEntity> data) {
        super(R.layout.item_media, data);
    }

    @Override
    protected void convert(ViewHolder helper, MediaEntity item) {
        helper.mTvName.setText(item.getDisplay_name());
        String size = String.format("%.2f", item.getSize() / 1024f / 1024f) + "M";
        helper.mIvIcon.setImageResource(R.mipmap.ic_launcher);
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_name)
        TextView mTvName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
