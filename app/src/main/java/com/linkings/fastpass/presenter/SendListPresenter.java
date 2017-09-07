package com.linkings.fastpass.presenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.adapter.SendListAdapter;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.activity.SendListActivity;
import com.linkings.fastpass.utils.FileInfoMG;

import java.util.List;

/**
 * Created by Lin on 2017/9/7.
 * Time: 15:47
 * Description: TOO
 */

public class SendListPresenter {

    private SendListActivity sendListActivity;

    public SendListPresenter(SendListActivity sendListActivity) {
        this.sendListActivity = sendListActivity;
    }

    public void init(RecyclerView recyclerview) {
        List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
        SendListAdapter sendListAdapter = new SendListAdapter(fileInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(sendListActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(sendListAdapter);
    }
}
