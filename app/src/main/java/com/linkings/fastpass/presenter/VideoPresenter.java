package com.linkings.fastpass.presenter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.VideoAdapter;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.fragment.VideoFragment;
import com.linkings.fastpass.utils.BitmapUtil;
import com.linkings.fastpass.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:35
 * Description: TOO
 */

public class VideoPresenter {
    private VideoFragment videoFragment;
    private List<FileInfo> mVideo;
    private VideoAdapter mVideoAdapter;

    public VideoPresenter(VideoFragment videoFragment) {
        this.videoFragment = videoFragment;
    }

    public void init(RecyclerView recyclerview) {
        mVideo = new ArrayList<>();
        mVideo = getVideoData(videoFragment.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(videoFragment.getContext());
        recyclerview.setLayoutManager(linearLayoutManager);
        mVideoAdapter = new VideoAdapter(mVideo);
        recyclerview.setAdapter(mVideoAdapter);
        mVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileInfo fileInfo = mVideo.get(position);
                fileInfo.setOK(!fileInfo.isOK());
                mVideoAdapter.notifyDataSetChanged();
            }
        });
        LogUtil.i(mVideo.size() + "");
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private static List<FileInfo> getVideoData(Context context) {
        List<FileInfo> list = new ArrayList<>();
        // 媒体库查询语句（写一个工具类MusicUtils）  
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfo mediaEntity = new FileInfo();
                mediaEntity.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
                mediaEntity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
                mediaEntity.setFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                mediaEntity.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                mediaEntity.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                mediaEntity.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST)));
                mediaEntity.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                Bitmap pic = BitmapUtil.getVideoThumbnail(mediaEntity.getFilePath());
                mediaEntity.setPic(BitmapUtil.bitmapToBase64(pic));
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
