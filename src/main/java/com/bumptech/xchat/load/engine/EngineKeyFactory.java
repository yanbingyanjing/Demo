package com.bumptech.xchat.load.engine;

import com.bumptech.xchat.load.Encoder;
import com.bumptech.xchat.load.Key;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.ResourceEncoder;
import com.bumptech.xchat.load.Transformation;
import com.bumptech.xchat.load.resource.transcode.ResourceTranscoder;

class EngineKeyFactory {

    @SuppressWarnings("rawtypes")
    public EngineKey buildKey(String id, Key signature, int width, int height, ResourceDecoder cacheDecoder,
            ResourceDecoder sourceDecoder, Transformation transformation, ResourceEncoder encoder,
            ResourceTranscoder transcoder, Encoder sourceEncoder) {
        return new EngineKey(id, signature, width, height, cacheDecoder, sourceDecoder, transformation, encoder,
                transcoder, sourceEncoder);
    }

}
