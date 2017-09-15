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

public class ApkAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {

    public ApkAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_apk, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        helper.setText(R.id.tv_name, item.getFileName());
        String size = OtherUtil.getFileSize(item.getSize());
        helper.setText(R.id.tv_size, size);
        helper.setImageBitmap(R.id.iv_icon, BitmapUtil.base64ToBitmap(item.getPic()));
        if (item.isOK()) {
            helper.setVisible(R.id.iv_ok, true);
        } else {
            helper.setVisible(R.id.iv_ok, false);
        }
    }
}
