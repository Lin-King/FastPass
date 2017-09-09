package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.utils.BitmapUtil;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class ApkAdapter extends BaseQuickAdapter<FileInfo, ApkAdapter.ViewHolder> {


    public ApkAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_apk, data);
    }

    @Override
    protected void convert(ViewHolder helper, FileInfo item) {
        helper.mTvName.setText(item.getFileName());
        String size = getFileSize(item.getSize());
        helper.mTvSize.setText(size);
        helper.mIvIcon.setImageBitmap(BitmapUtil.base64ToBitmap(item.getPic()));
        if (item.isOK()) {
            helper.mIvOk.setVisibility(View.VISIBLE);
        } else {
            helper.mIvOk.setVisibility(View.GONE);
        }
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("####.##");

    private static String getFileSize(long size) {
        if (size < 0) { //小于0字节则返回0
            return "0B";
        }
        double value;
        if ((size / 1024) < 1) { //0 ` 1024 byte
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {//0 ` 1024 kbyte

            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100L / (1024L * 1024L * 1024L)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.iv_ok)
        ImageView mIvOk;
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
