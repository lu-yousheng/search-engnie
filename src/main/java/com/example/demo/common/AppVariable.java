package com.example.demo.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局变量
 */
public class AppVariable {
    //用具session key
    public static final String USER_SESSION_KEY = "USER_SESSION_KEY";

    //头像大小限制
    public static final int PHOTO_MAX_SIZE = 10 * 1024 * 1024;

    public static final List<String> PHOTO_TYPE = new ArrayList<>();

    static {
        PHOTO_TYPE.add("image/jpg");
        PHOTO_TYPE.add("image/jpeg");
        PHOTO_TYPE.add("image/png");
        PHOTO_TYPE.add("image/bmp");
    }
}
