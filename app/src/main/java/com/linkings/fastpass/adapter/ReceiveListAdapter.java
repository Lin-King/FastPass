package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;

import java.util.List;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class ReceiveListAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {


    public ReceiveListAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_filereceivelist, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        helper.setText(R.id.tv_name, item.getFileName());
        String progress = item.getProgress() + "%";
        helper.setText(R.id.tv_progress, progress);
//        helper.mIvShortcut.setImageBitmap(BitmapUtil.base64ToBitmap(item.getPic()));
        helper.setProgress(R.id.pb_file, item.getProgress());
        helper.addOnClickListener(R.id.btn_operation);
    }
}
