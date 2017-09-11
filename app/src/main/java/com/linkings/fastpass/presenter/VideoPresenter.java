package com.linkings.fastpass.presenter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.VideoAdapter;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.fragment.VideoFragment;
import com.linkings.fastpass.utils.BitmapUtil;
import com.linkings.fastpass.utils.LogUtil;

import java.lang.ref.WeakReference;
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
    private MyHandler mMyHandler;

    public VideoPresenter(VideoFragment videoFragment) {
        this.videoFragment = videoFragment;
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<VideoPresenter> activityWeakReference;

        MyHandler(VideoPresenter mVideoPresenter) {
            activityWeakReference = new WeakReference<>(mVideoPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPresenter mVideoPresenter = activityWeakReference.get();
            if (mVideoPresenter != null) {
                switch (msg.what) {
                    case Constant.MSG_UPDATE_ADAPTER:
                        if (mVideoPresenter.mVideoAdapter != null) {
                            mVideoPresenter.mVideoAdapter.notifyDataSetChanged();
                            mVideoPresenter.videoFragment.setNum(mVideoPresenter.mVideo.size());
                        }
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerview) {
        videoFragment.showProgress("");
        mVideo = new ArrayList<>();
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
        readVideo();
    }

    private void readVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mVideo != null) {
                    mVideo.clear();
                    mVideo.addAll(getVideoData(videoFragment.getContext()));
                    LogUtil.i(mVideo.size() + "");
                    mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
                    videoFragment.hideProgress();
                }
            }
        }).start();
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private static List<FileInfo> getVideoData(Context context) {
        List<FileInfo> list = new ArrayList<>();
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
                mediaEntity.setDate(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                Bitmap pic = BitmapUtil.getVideoThumbnail(mediaEntity.getFilePath());
                mediaEntity.setPic(BitmapUtil.bitmapToBase64(pic));
                if (mediaEntity.getSize() > 1000 * 800) {
                    if (mediaEntity.getTitle().contains("-")) {
                        String[] str = mediaEntity.getTitle().split("-");
                        mediaEntity.setArtist(str[0]);
                        mediaEntity.setTitle(str[1]);
                    }
                    list.add(mediaEntity);
                }
            }
            cursor.close();
        }
        return list;
    }
}
