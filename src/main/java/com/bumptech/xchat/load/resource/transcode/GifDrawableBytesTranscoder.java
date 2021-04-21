package com.bumptech.xchat.load.resource.transcode;

import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.resource.bytes.BytesResource;
import com.bumptech.xchat.load.resource.gif.GifDrawable;
import com.bumptech.xchat.load.engine.Resource;
import com.bumptech.xchat.load.resource.bytes.BytesResource;
import com.bumptech.xchat.load.resource.gif.GifDrawable;

/**
 * An {@link ResourceTranscoder} that converts
 * {@link GifDrawable} into bytes by obtaining the original bytes of the GIF from
 * the {@link GifDrawable}.
 */
public class GifDrawableBytesTranscoder implements ResourceTranscoder<GifDrawable, byte[]> {
    @Override
    public Resource<byte[]> transcode(Resource<GifDrawable> toTranscode) {
        GifDrawable gifData = toTranscode.get();
        return new BytesResource(gifData.getData());
    }

    @Override
    public String getId() {
        return "GifDrawableBytesTranscoder.com.bumptech.xchat.load.resource.transcode";
    }
}
