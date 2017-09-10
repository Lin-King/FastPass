package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.utils.OtherUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class MediaAdapter extends BaseQuickAdapter<FileInfo, MediaAdapter.ViewHolder> {

    public MediaAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_media, data);
    }

    @Override
    protected void convert(ViewHolder holder, FileInfo item) {
        holder.mTvName.setText(item.getFileName());
        holder.mTvSinger.setText(item.getArtist());
        int duration = item.getDuration();
        String time = OtherUtil.formatTime(duration);
        holder.mTvDuration.setText(time);
        String size = OtherUtil.getFileSize(item.getSize());
        holder.mTvSize.setText(size);
        if (item.isOK()) {
            holder.mIvOk.setVisibility(View.VISIBLE);
        } else {
            holder.mIvOk.setVisibility(View.GONE);
        }
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_ok)
        ImageView mIvOk;
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_singer)
        TextView mTvSinger;
        @BindView(R.id.tv_duration)
        TextView mTvDuration;
        @BindView(R.id.tv_size)
        TextView mTvSize;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
