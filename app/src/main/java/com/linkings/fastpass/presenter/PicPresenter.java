package com.linkings.fastpass.presenter;

import android.content.ContentResolver;
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
                            LogUtil.i(mPicPresenter.mPic.size() + "");
                            mPicPresenter.mPicAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerview) {
        mPic = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(picFragment.getContext());
        recyclerview.setLayoutManager(linearLayoutManager);
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
        readPic();
    }

    private void readPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPic.clear();
                mPic.addAll(getVideoData(picFragment.getContext()));
                mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
                picFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPicAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
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
                if (mediaEntity.getSize() > 1000 * 100) {
//                    Bitmap pic = getImageThumbnail(context, context.getContentResolver(), mediaEntity.getFilePath());
                    //得到原图片  
                    Bitmap pic = BitmapFactory.decodeFile(mediaEntity.getFilePath());
                    //得到缩略图  
                    pic = ThumbnailUtils.extractThumbnail(pic, 100, 100);
                    mediaEntity.setPic(BitmapUtil.bitmapToBase64(pic));
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

    private static Bitmap getImageThumbnail(Context context, ContentResolver cr, String Imagepath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID,};
        String whereClause = MediaStore.Images.Media.DATA + " = '" + Imagepath + "'";
        Cursor cursor = testcr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String imagePath = "";
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {

            int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do {
                _id = cursor.getInt(_idColumn);
                imagePath = cursor.getString(_dataColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, MediaStore.Images.Thumbnails.MINI_KIND,
                options);
        return bitmap;
    }
}
