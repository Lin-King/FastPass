package com.linkings.fastpass.presenter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.PicAdapter;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.fragment.PicFragment;
import com.linkings.fastpass.utils.BitmapUtil;
import com.linkings.fastpass.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:39
 * Description: TOO
 */

public class PicPresenter {
    private MyHandler mMyHandler;
    private PicFragment picFragment;
    private List<FileInfo> mPic;
    private PicAdapter mPicAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public PicPresenter(PicFragment picFragment) {
        this.picFragment = picFragment;
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<PicPresenter> activityWeakReference;

        MyHandler(PicPresenter mPicPresenter) {
            activityWeakReference = new WeakReference<>(mPicPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            PicPresenter mPicPresenter = activityWeakReference.get();
            if (mPicPresenter != null) {
                switch (msg.what) {
                    case Constant.MSG_UPDATE_ADAPTER:
                        if (mPicPresenter.mPicAdapter != null) {
                            mPicPresenter.mPicAdapter.notifyDataSetChanged();
                            mPicPresenter.picFragment.setNum(mPicPresenter.mPic.size());
                        }
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerview) {
        picFragment.showProgress("");
        mPic = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(picFragment.getContext());
        recyclerview.setLayoutManager(mLinearLayoutManager);
        mPicAdapter = new PicAdapter(mPic);
        recyclerview.setAdapter(mPicAdapter);
        mPicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileInfo fileInfo = mPic.get(position);
                fileInfo.setOK(!fileInfo.isOK());
                mPicAdapter.notifyDataSetChanged();
            }
        });
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    setPicVisit();
                }
            }
        });
        readPic();
    }

    private void setPicVisit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
//                LogUtil.i("firstVisibleItemPosition " + firstVisibleItemPosition);
//                LogUtil.i("lastVisibleItemPosition " + lastVisibleItemPosition);
                if (mPic != null && firstVisibleItemPosition >= 0) {
                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        FileInfo fileInfo = mPic.get(i);
                        if (TextUtils.isEmpty(fileInfo.getPic())) {
                            //得到原图片  
                            Bitmap pic = BitmapFactory.decodeFile(fileInfo.getFilePath());
                            //得到缩略图  
                            pic = ThumbnailUtils.extractThumbnail(pic, 100, 100);
                            fileInfo.setPic(BitmapUtil.bitmapToBase64(pic));
                        }
                        mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
                    }
                }
            }
        }).start();
    }

    private void readPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPic.clear();
                mPic.addAll(getVideoData(picFragment.getContext()));
                LogUtil.i(mPic.size() + "");
                mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
                picFragment.hideProgress();
            }
        }).start();
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    private static List<FileInfo> getVideoData(Context context) {
        List<FileInfo> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfo mediaEntity = new FileInfo();
                mediaEntity.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
                mediaEntity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
                mediaEntity.setFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                mediaEntity.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                mediaEntity.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                mediaEntity.setDate(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                if (mediaEntity.getSize() > 1000 * 100) {
                    if (list.size() < 10) {
                        //得到原图片  
                        Bitmap pic = BitmapFactory.decodeFile(mediaEntity.getFilePath());
                        //得到缩略图  
                        pic = ThumbnailUtils.extractThumbnail(pic, 100, 100);
                        mediaEntity.setPic(BitmapUtil.bitmapToBase64(pic));
                    }
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
