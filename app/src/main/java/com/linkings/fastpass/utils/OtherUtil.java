package com.linkings.fastpass.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;

import java.text.DecimalFormat;

/**
 * Created by Lin on 2017/9/10.
 * Time: 15:03
 * Description: TOO
 */

public class OtherUtil {

    private static final DecimalFormat FORMAT = new DecimalFormat("####.##");

    public static String getFileSize(long size) {
        if (size < 0) { //小于0字节则返回0
            return "0B";
        }
        double value;
        if ((size / 1024) < 1) { //0 ` 1024 byte
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {//0 ` 1024 kbyte

            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100L / (1024L * 1024L * 1024L)) / 100f;
            return FORMAT.format(value) + "GB";
        }
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
