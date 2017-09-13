package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.FileInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class ReceiveListAdapter extends BaseQuickAdapter<FileInfo, ReceiveListAdapter.ViewHolder> {


    public ReceiveListAdapter(@Nullable List<FileInfo> data) {
        super(R.layout.item_filereceivelist, data);
    }

    @Override
    protected void convert(ViewHolder helper, FileInfo item) {
        helper.mTvName.setText(item.getFileName());
        String progress = item.getProgress() + "%";
        helper.mTvProgress.setText(progress);
//        helper.mIvShortcut.setImageBitmap(BitmapUtil.base64ToBitmap(item.getPic()));
        helper.mPbFile.setProgress(item.getProgress());
        helper.addOnClickListener(R.id.btn_operation);
    }

    static class ViewHolder extends BaseViewHolder {
//        @BindView(R.id.iv_shortcut)
//        ImageView mIvShortcut;
        @BindView(R.id.btn_operation)
        Button mBtnOperation;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_progress)
        TextView mTvProgress;
        @BindView(R.id.pb_file)
        ProgressBar mPbFile;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
