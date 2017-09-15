package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.utils.BitmapUtil;
import com.linkings.fastpass.utils.OtherUtil;

import java.util.List;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class VideoAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {

    public VideoAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_video, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, FileInfo item) {
        holder.setImageBitmap(R.id.iv_icon, BitmapUtil.base64ToBitmap(item.getPic()));
        holder.setText(R.id.tv_name, item.getFileName());
        int duration = item.getDuration();
        String time = OtherUtil.formatTime(duration);
        holder.setText(R.id.tv_duration, time);
        String size = OtherUtil.getFileSize(item.getSize());
        holder.setText(R.id.tv_size, size);
        if (item.isOK()) {
            holder.setVisible(R.id.iv_ok, true);
        } else {
            holder.setVisible(R.id.iv_ok, false);
        }
    }

}
