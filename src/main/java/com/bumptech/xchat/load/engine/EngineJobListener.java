package com.bumptech.xchat.load.engine;

import com.bumptech.xchat.load.Key;
import com.bumptech.xchat.load.Key;

interface EngineJobListener {

    void onEngineJobComplete(Key key, EngineResource<?> resource);

    void onEngineJobCancelled(EngineJob engineJob, Key key);
}
