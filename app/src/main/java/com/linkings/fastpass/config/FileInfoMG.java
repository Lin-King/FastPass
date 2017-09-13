package com.linkings.fastpass.config;

import android.os.Build;
import android.util.ArrayMap;

import com.linkings.fastpass.model.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lin on 2017/9/7.
 * Time: 10:03
 * Description: TOO
 */

public class FileInfoMG {

    private List<FileInfo> mFileInfoList;
    private Map<String, FileInfo> mFileInfoMap;
    private boolean isClear;

    private FileInfoMG() {
        mFileInfoList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mFileInfoMap = new ArrayMap<>();
        } else {
            mFileInfoMap = new HashMap<>();
        }
    }

    public static final FileInfoMG getInstance() {
        return MyInstance.INSTANCE;
    }

    private static class MyInstance {
        private static final FileInfoMG INSTANCE = new FileInfoMG();
    }

    public List<FileInfo> getFileInfoList() {
        mFileInfoList.clear();
        for (Map.Entry<String, FileInfo> entry : mFileInfoMap.entrySet()) {
            mFileInfoList.add(entry.getValue());
        }
        return mFileInfoList;
    }

    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }

    public int getListSize() {
        return mFileInfoMap.size();
    }

    public void cleanFileInfoList() {
        mFileInfoMap.clear();
        setClear(true);
    }

    public void removeFileInfo(FileInfo fileInfo) {
        mFileInfoMap.remove(fileInfo.getFilePath());
    }

    public void addFileInfo(FileInfo fileInfo) {
        mFileInfoMap.put(fileInfo.getFilePath(), fileInfo);
    }
}
