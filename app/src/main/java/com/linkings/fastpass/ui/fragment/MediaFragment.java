package com.linkings.fastpass.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.MediaAdapter;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.model.MediaEntity;
import com.linkings.fastpass.presenter.MediaPresenter;
import com.linkings.fastpass.ui.interfaces.IMediaView;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/4.
 * Time: 10:29
 * Description: TOO
 */

public class MediaFragment extends BaseFragment implements IMediaView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private MediaPresenter mMediaPresenter;
    private List<MediaEntity> mMp3;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_media;
    }

    @Override
    public void initPresenter() {
        mMediaPresenter = new MediaPresenter(this);
    }

    @Override
    public void initView() {
        mMp3 = new ArrayList<>();
        mMp3 = getMusicData(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        MediaAdapter mediaAdapter = new MediaAdapter(mMp3);
        mRecyclerview.setAdapter(mediaAdapter);
        mediaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtil.show(context, mMp3.get(position).getPath());
            }
        });

        LogUtil.i(mMp3.size() + "");
    }

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<MediaEntity> getMusicData(Context context) {
        List<MediaEntity> list = new ArrayList<MediaEntity>();
        // 媒体库查询语句（写一个工具类MusicUtils）  
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MediaEntity mediaEntity = new MediaEntity();
                mediaEntity.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                mediaEntity.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                mediaEntity.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                mediaEntity.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                mediaEntity.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                mediaEntity.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                mediaEntity.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
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

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }
}
