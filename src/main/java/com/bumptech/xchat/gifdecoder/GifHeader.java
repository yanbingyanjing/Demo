//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bumptech.xchat.gifdecoder;

import java.util.ArrayList;
import java.util.List;

public class GifHeader {
    int[] gct = null;
    int status = 0;
    int frameCount = 0;
    GifFrame currentFrame;
    List<GifFrame> frames = new ArrayList();
    int width;
    int height;
    boolean gctFlag;
    int gctSize;
    int bgIndex;
    int pixelAspect;
    int bgColor;
    int loopCount;

    public GifHeader() {
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getNumFrames() {
        return this.frameCount;
    }

    public int getStatus() {
        return this.status;
    }
}
