package com.bumptech.xchat.load.model.stream;

import android.content.Context;

import com.bumptech.xchat.load.data.DataFetcher;
import com.bumptech.xchat.load.data.HttpUrlFetcher;
import com.bumptech.xchat.load.model.GenericLoaderFactory;
import com.bumptech.xchat.load.model.GlideUrl;
import com.bumptech.xchat.load.model.ModelCache;
import com.bumptech.xchat.load.model.ModelLoader;
import com.bumptech.xchat.load.model.ModelLoaderFactory;
import com.bumptech.xchat.load.data.DataFetcher;
import com.bumptech.xchat.load.data.HttpUrlFetcher;

import java.io.InputStream;

/**
 * An {@link ModelLoader} for translating {@link GlideUrl}
 * (http/https URLS) into {@link InputStream} data.
 */
public class HttpUrlGlideUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final ModelCache<GlideUrl, GlideUrl> modelCache;

    /**
     * The default factory for {@link HttpUrlGlideUrlLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private final ModelCache<GlideUrl, GlideUrl> modelCache = new ModelCache<GlideUrl, GlideUrl>(500);

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new HttpUrlGlideUrlLoader(modelCache);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }

    public HttpUrlGlideUrlLoader() {
        this(null);
    }

    public HttpUrlGlideUrlLoader(ModelCache<GlideUrl, GlideUrl> modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        // GlideUrls memoize parsed URLs so caching them saves a few object instantiations and time spent parsing urls.
        GlideUrl url = model;
        if (modelCache != null) {
            url = modelCache.get(model, 0, 0);
            if (url == null) {
                modelCache.put(model, 0, 0, model);
                url = model;
            }
        }
        return new HttpUrlFetcher(url);
    }
}
