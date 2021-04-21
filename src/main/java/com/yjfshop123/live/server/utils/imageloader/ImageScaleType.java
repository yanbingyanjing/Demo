package com.yjfshop123.live.server.utils.imageloader;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public enum ImageScaleType {
    NONE,
    NONE_SAFE,
    IN_SAMPLE_POWER_OF_2,
    IN_SAMPLE_INT,
    EXACTLY,
    EXACTLY_STRETCHED;

    private ImageScaleType() {
    }
}
