package com.bumptech.xchat.load.model.stream;

import android.content.Context;
import android.net.Uri;

import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.model.GenericLoaderFactory;
import com.bumptech.xchat.load.model.ModelLoader;
import com.bumptech.xchat.load.model.ModelLoaderFactory;
import com.bumptech.xchat.load.model.ResourceLoader;
import com.bumptech.xchat.Glide;

import java.io.InputStream;

/**
 * A {@link ModelLoader} For translating android resource id models for local uris into {@link InputStream} data.
 */
public class StreamResourceLoader extends ResourceLoader<InputStream> implements StreamModelLoader<Integer> {

    /**
     * The default factory for {@link StreamResourceLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<Integer, InputStream> {

        @Override
        public ModelLoader<Integer, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new StreamResourceLoader(context, factories.buildModelLoader(Uri.class, InputStream.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }

    public StreamResourceLoader(Context context) {
        this(context, Glide.buildStreamModelLoader(Uri.class, context));
    }

    public StreamResourceLoader(Context context, ModelLoader<Uri, InputStream> uriLoader) {
        super(context, uriLoader);
    }
}
