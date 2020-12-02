package com.sample.icontest;

/**
 * @author LiaoLiang
 * @date : 2020/12/2 17:21
 */
public class StringUtil {

    public static String getBytesString(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < (1048576)) {
            float kb = bytes/1024f;
            return kb + " KB";
        }
        float mb = bytes / 1048576f;
        return mb + " MB";
    }

}
