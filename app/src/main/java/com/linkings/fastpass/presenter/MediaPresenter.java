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
import com.linkings.fastpass.ui.fragment.MediaFragment;
import com.linkings.fastpass.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/4.
 * Time: 10:30
 * Description: TOO
 */

public class MediaPresenter {

    private List<FileInfo> mMp3;
    private MediaFragment mediaFragment;
    private Context context;
    private MediaAdapter mMediaAdapter;

    public MediaPresenter(MediaFragment mediaFragment) {
        this.mediaFragment = mediaFragment;
        this.context = mediaFragment.getActivity();
    }

    public void init(RecyclerView recyclerview) {
        mMp3 = new ArrayList<>();
        mMp3 = getMusicData(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(linearLayoutManager);
        mMediaAdapter = new MediaAdapter(mMp3);
        recyclerview.setAdapter(mMediaAdapter);
        mMediaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileInfo fileInfo = mMp3.get(position);
                fileInfo.setOK(!fileInfo.isOK());
                mMediaAdapter.notifyDataSetChanged();
            }
        });
        LogUtil.i(mMp3.size() + "");
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private static List<FileInfo> getMusicData(Context context) {
        List<FileInfo> list = new ArrayList<>();
        // 媒体库查询语句（写一个工具类MusicUtils）  
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfo mediaEntity = new FileInfo();
                mediaEntity.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                mediaEntity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                mediaEntity.setFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                mediaEntity.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                mediaEntity.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                mediaEntity.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                mediaEntity.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                if (mediaEntity.getSize() > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）  
                    if (mediaEntity.getTitle().contains("-")) {
                        String[] str = mediaEntity.getTitle().split("-");
                        mediaEntity.setArtist(str[0]);
                        mediaEntity.setTitle(str[1]);
                    }
                    list.add(mediaEntity);
                }
            }
            // 释放资源  
            cursor.close();
        }
        return list;
    }

}

