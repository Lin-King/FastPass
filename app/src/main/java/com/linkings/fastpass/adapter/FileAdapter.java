package com.linkings.fastpass.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;

import java.io.File;
import java.util.List;

/**
 * Created by Lin on 2017/8/11.
 * Time: 13:34
 * Description: TOO
 */

public class FileAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {

    public FileAdapter(List<FileInfo> data) {
        super(R.layout.item_file, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        helper.setText(R.id.tv_name, item.getFileName());
        if (new File(item.getFilePath()).isDirectory()) {
            helper.setVisible(R.id.iv_pic, true);
        } else {
            helper.setVisible(R.id.iv_pic, false);
        }
        if (item.isOK()) {
            helper.setVisible(R.id.iv_ok, true);
        } else {
            helper.setVisible(R.id.iv_ok, false);
        }
    }
}
