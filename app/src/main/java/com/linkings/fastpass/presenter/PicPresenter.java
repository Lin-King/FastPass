package com.linkings.fastpass.presenter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.MediaAdapter;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.fragment.PicFragment;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:39
 * Description: TOO
 */

public class PicPresenter {
    private PicFragment picFragment;
    private List<FileInfo> mPic;

    public PicPresenter(PicFragment picFragment) {
        this.picFragment = picFragment;
    }

    public void init(RecyclerView recyclerview) {
        mPic = new ArrayList<>();
        mPic = getVideoData(picFragment.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(picFragment.getContext());
        recyclerview.setLayoutManager(linearLayoutManager);
        MediaAdapter mediaAdapter = new MediaAdapter(mPic);
        recyclerview.setAdapter(mediaAdapter);
        mediaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtil.show(picFragment.getContext(), mPic.get(position).getFilePath());
            }
        });
        LogUtil.i(mPic.size() + "");
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private static List<FileInfo> getVideoData(Context context) {
        List<FileInfo> list = new ArrayList<>();
        // 媒体库查询语句（写一个工具类MusicUtils）  
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfo mediaEntity = new FileInfo();
                mediaEntity.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
                mediaEntity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
                mediaEntity.setFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                mediaEntity.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                mediaEntity.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                if (mediaEntity.getTitle().contains("-")) {
                    String[] str = mediaEntity.getTitle().split("-");
                    mediaEntity.setArtist(str[0]);
                    mediaEntity.setTitle(str[1]);
                }
                list.add(mediaEntity);
            }
            // 释放资源  
            cursor.close();
        }
        return list;
    }
}
