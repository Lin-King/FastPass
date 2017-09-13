package com.linkings.fastpass.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.MediaAdapter;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.activity.HomeActivity;
import com.linkings.fastpass.ui.fragment.MediaFragment;
import com.linkings.fastpass.utils.LogUtil;

import java.lang.ref.WeakReference;
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
    private MyHandler mMyHandler;

    public MediaPresenter(MediaFragment mediaFragment) {
        this.mediaFragment = mediaFragment;
        this.context = mediaFragment.getActivity();
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<MediaPresenter> activityWeakReference;

        MyHandler(MediaPresenter mMediaPresenter) {
            activityWeakReference = new WeakReference<>(mMediaPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaPresenter mMediaPresenter = activityWeakReference.get();
            if (mMediaPresenter != null) {
                switch (msg.what) {
                    case Constant.MSG_UPDATE_ADAPTER:
                        if (mMediaPresenter.mMediaAdapter != null) {
                            mMediaPresenter.mMediaAdapter.notifyDataSetChanged();
                            mMediaPresenter.mediaFragment.setNum(mMediaPresenter.mMp3.size());
                        }
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerview) {
        mediaFragment.showProgress("");
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
                if (fileInfo.isOK()) FileInfoMG.getInstance().addFileInfo(fileInfo);
                else FileInfoMG.getInstance().removeFileInfo(fileInfo);
                mMediaAdapter.notifyDataSetChanged();
                ((HomeActivity) mediaFragment.getActivity()).setSendNum();
            }
        });
        readMp3();
    }

    private void readMp3() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMp3 != null) {
                    mMp3.clear();
                    mMp3.addAll(getMusicData(mediaFragment.getContext()));
                    LogUtil.i(mMp3.size() + "");
                    mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
                    mediaFragment.hideProgress();
                }
            }
        }).start();
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private static List<FileInfo> getMusicData(Context context) {
        List<FileInfo> list = new ArrayList<>();
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
                mediaEntity.setDate(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
                mediaEntity.setFileType(mediaEntity.getFilePath().substring(mediaEntity.getFilePath().lastIndexOf(".") + 1));
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

