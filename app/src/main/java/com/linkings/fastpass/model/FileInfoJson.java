package com.linkings.fastpass.model;

import java.util.List;

/**
 * Created by Lin on 2017/9/7.
 * Time: 17:32
 * Description: TOO
 */

public class FileInfoJson {
    private List<FileInfo> mFileInfos;

    public FileInfoJson(List<FileInfo> mAllFileInfos) {
        this.mFileInfos = mAllFileInfos;
    }

    public List<FileInfo> getFileInfos() {
        return mFileInfos;
    }

    public void setFileInfos(List<FileInfo> fileInfos) {
        mFileInfos = fileInfos;
    }
}
