package com.yjfshop123.live.authid;

import com.google.common.hash.Hashing;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.net.utils.NLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.text.Charsets;

public class SignUtil {
    public static String signData() {
        String sign = "";
        return sign;
    }

    public static String sign(FaceVerfyProcess faceVerfyProcess, String ticket) {
        List<String> values = new ArrayList<>();

        values.removeAll(Collections.singleton(null));// remove null

        values.add(faceVerfyProcess.getWebankAppId());
        NLog.d("人脸核身签名书数据getWebankAppId", faceVerfyProcess.getWebankAppId());
        values.add(faceVerfyProcess.getUserId());
        NLog.d("人脸核身签名书数据getUserId", faceVerfyProcess.getUserId());
        values.add(faceVerfyProcess.getVersion());
        NLog.d("人脸核身签名书数据getVersion", faceVerfyProcess.getVersion());
        values.add(ticket);
        NLog.d("人脸核身签名书数据ticket", ticket);
        values.add(faceVerfyProcess.getNonce());
        NLog.d("人脸核身签名书数据getNonce", faceVerfyProcess.getNonce());
        java.util.Collections.sort(values);

        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }

        //   MessageDigest sha1 = null;
//        try {
//            sha1 = MessageDigest.getInstance("SHA1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        if(sha1==null)return "";
        return Hashing.sha1().hashString(sb, Charsets.UTF_8).toString().toUpperCase();
        // return Arrays.toString(sha1.digest(sb.toString().getBytes())).toUpperCase();
    }

    public static String signTest() {
        List<String> values = new ArrayList<>();

        values.removeAll(Collections.singleton(null));// remove null

        values.add(Const.faceVerifyappid);
        values.add("8");

        values.add(Const.version);
        values.add("wDuxk5dv9bwfrMiYfL36Wh6pLNnjwTEVAQFFkFsgBUea2c4jmHaX84J1zPqcmuHy");
        values.add("584b0nKjxk2MzrsNuwcULFGlqSIo7EYp");
        java.util.Collections.sort(values);

        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }

        //   MessageDigest sha1 = null;
//        try {
//            sha1 = MessageDigest.getInstance("SHA1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        if(sha1==null)return "";
        return Hashing.sha1().hashString(sb, Charsets.UTF_8).toString().toUpperCase();
        // return Arrays.toString(sha1.digest(sb.toString().getBytes())).toUpperCase();
    }
}
