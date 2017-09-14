package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.utils.BitmapUtil;
import com.linkings.fastpass.utils.OtherUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class PicAdapter extends BaseQuickAdapter<FileInfo, PicAdapter.ViewHolder> {

    public PicAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_pic, data);
    }

    @Override
    protected void convert(ViewHolder holder, FileInfo item) {
        if (!TextUtils.isEmpty(item.getPic())) {
            holder.mIvIcon.setImageBitmap(BitmapUtil.base64ToBitmap(item.getPic()));
        } else {
            holder.mIvIcon.setImageResource(R.mipmap.ic_music_note_red_100_24dp);
        }
        holder.mTvName.setText(item.getFileName());
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
