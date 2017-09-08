package com.linkings.fastpass.config;

import com.linkings.fastpass.model.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/7.
 * Time: 10:03
 * Description: TOO
 */

public class FileInfoMG {

    private List<FileInfo> mFileInfoList;

    private FileInfoMG() {
        mFileInfoList = new ArrayList<>();
    }

    public static final FileInfoMG getInstance() {
        return MyInstance.INSTANCE;
    }

    private static class MyInstance {
        private static final FileInfoMG INSTANCE = new FileInfoMG();
    }

    public List<FileInfo> getFileInfoList() {
        return mFileInfoList;
    }

    public void cleanFileInfoList() {
        mFileInfoList.clear();
    }
}
