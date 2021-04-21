package com.yjfshop123.live.live.live.common.widget.pk;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yjfshop123.live.App;
import com.yjfshop123.live.live.live.common.widget.beautysetting.utils.IOUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;

public class BitmapUtil {
    private static BitmapUtil sInstance;
    private Resources mResources;
    private BitmapFactory.Options mOptions;

    private BitmapUtil() {
        mResources = App.getInstance().getResources();
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inDither=true;
        mOptions.inSampleSize = 1;
    }

    public static BitmapUtil getInstance() {
        if (sInstance == null) {
            synchronized (BitmapUtil.class) {
                if (sInstance == null) {
                    sInstance = new BitmapUtil();
                }
            }
        }
        return sInstance;
    }


    public Bitmap decodeBitmap(int imgRes) {
        Bitmap bitmap = null;
        try {
            byte[] bytes = IOUtils.toByteArray(mResources.openRawResource(imgRes));
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SoftReference<>(bitmap).get();
    }
}
