package com.bumptech.xchat.load.resource.gif;

import com.bumptech.xchat.Priority;
import com.bumptech.xchat.gifdecoder.GifDecoder;
import com.bumptech.xchat.load.data.DataFetcher;
import com.bumptech.xchat.load.model.ModelLoader;
import com.bumptech.xchat.Priority;
import com.bumptech.xchat.gifdecoder.GifDecoder;
import com.bumptech.xchat.load.data.DataFetcher;
import com.bumptech.xchat.load.model.ModelLoader;

class GifFrameModelLoader implements ModelLoader<GifDecoder, GifDecoder> {

    @Override
    public DataFetcher<GifDecoder> getResourceFetcher(GifDecoder model, int width, int height) {
        return new GifFrameDataFetcher(model);
    }

    private static class GifFrameDataFetcher implements DataFetcher<GifDecoder> {
        private final GifDecoder decoder;

        public GifFrameDataFetcher(GifDecoder decoder) {
            this.decoder = decoder;
        }

        @Override
        public GifDecoder loadData(Priority priority) {
            return decoder;
        }

        @Override
        public void cleanup() {
            // Do nothing. GifDecoder reads from an arbitrary InputStream, the caller will close that stream.
        }

        @Override
        public String getId() {
            return String.valueOf(decoder.getCurrentFrameIndex());
        }

        @Override
        public void cancel() {
            // Do nothing.
        }
    }
}
