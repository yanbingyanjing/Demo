package com.yjfshop123.live.server.utils.imageloader;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class HashCodeFileNameGenerator implements FileNameGenerator {
    public HashCodeFileNameGenerator() {
    }

    public String generate(String imageUri) {
        return String.valueOf(imageUri.hashCode());
    }
}
