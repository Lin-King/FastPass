package com.linkings.fastpass.model;

import java.io.Serializable;

/**
 * Created by Lin on 2017/9/6.
 * Time: 16:33
 * Description: TOO
 */

public class FileInfo implements Serializable {

    /**
     * 文件传输结果：1 成功  -1 失败
     */
    public static final int FLAG_SUCCESS = 1;
    public static final int FLAG_FAILURE = -1;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private int fileType;

    /**
     * 文件大小
     */
    private long size;

    /***
     * 文件名
     */
    private String fileName;

    /**
     * 文件传送结果
     */
    private int result;

    /**
     * 传输进度
     */
    private int progress;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
