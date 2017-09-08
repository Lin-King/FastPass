package com.linkings.fastpass.model;

import java.io.Serializable;

/**
 * Created by Lin on 2017/9/6.
 * Time: 16:33
 * Description: TOO
 */

public class FileInfo implements Serializable {

    public static final int FLAG_SUCCESS = 1;//文件传输结果：1 成功
    public static final int FLAG_FAILURE = -1;//文件传输结果：-1 失败
    private String filePath;//文件路径
    private String fileType;//文件类型
    private long size;//文件大小
    private String fileName;//文件名
    private int result;//文件传送结果
    private int progress;//传输进度
    //Apk
    private String pic;
    //mp3
    private int id; //id标识  
    private String title; // 显示名称  
    private int duration; // 媒体播放总时间  
    private String albums; // 专辑  
    private String artist; // 艺术家   
    private String singer; //歌手   

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
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
