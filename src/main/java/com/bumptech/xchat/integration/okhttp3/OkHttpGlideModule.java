package com.bumptech.xchat.integration.okhttp3;

import android.content.Context;

import com.bumptech.xchat.Glide;
import com.bumptech.xchat.GlideBuilder;
import com.bumptech.xchat.load.model.GlideUrl;
import com.bumptech.xchat.module.GlideModule;

import java.io.InputStream;

/**
 * A {@link com.bumptech.xchat.module.GlideModule} implementation to replace Glide's default
 * {@link java.net.HttpURLConnection} based {@link com.bumptech.xchat.load.model.ModelLoader}
 * with an OkHttp based {@link com.bumptech.xchat.load.model.ModelLoader}.
 * <p/>
 * <p> If you're using gradle, you can include this module simply by depending on the aar, the
 * module will be merged in by manifest merger. For other build systems or for more more
 * information, see {@link com.bumptech.xchat.module.GlideModule}. </p>
 */
public class OkHttpGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Do nothing.
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
