package com.linkings.fastpass.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Lin on 2017/9/3.
 * Time: 16:19
 * Description: TOO
 */

public class Apk {

    private Drawable pic;
    private String name;
    private long size;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Drawable getPic() {
        return pic;
    }

    public void setPic(Drawable pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
