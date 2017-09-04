package com.linkings.fastpass.model;

import java.io.Serializable;

/**
 * Created by Lin on 2017/9/4.
 * Time: 10:28
 * Description: TOO
 */

public class MediaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id; //id标识  
    private String title; // 显示名称  
    private String display_name; // 文件名称  
    private String path; // 音乐文件的路径  
    private int duration; // 媒体播放总时间  
    private String albums; // 专辑  
    private String artist; // 艺术家   
    private String singer; //歌手   
    private long size;

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

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}  
