package com.linkings.fastpass.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;
import com.linkings.fastpass.model.Apk;

import java.util.List;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class ApkAdapter extends BaseQuickAdapter<Apk,ApkAdapter.MyViewHolder> {


    public ApkAdapter(@Nullable List<Apk> data) {
        super(R.layout.item_apk, data);
    }

    @Override
    protected void convert(MyViewHolder helper, Apk item) {
        
    }

    class MyViewHolder extends BaseViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }
    }
}
