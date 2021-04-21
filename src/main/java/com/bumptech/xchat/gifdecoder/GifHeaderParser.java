//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bumptech.xchat.gifdecoder;

import android.util.Log;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class GifHeaderParser {
    public static final String TAG = "GifHeaderParser";
    static final int MIN_FRAME_DELAY = 3;
    static final int DEFAULT_FRAME_DELAY = 10;
    private static final int MAX_BLOCK_SIZE = 256;
    private final byte[] block = new byte[256];
    private ByteBuffer rawData;
    private GifHeader header;
    private int blockSize = 0;

    public GifHeaderParser() {
    }

    public GifHeaderParser setData(byte[] data) {
        this.reset();
        if (data != null) {
            this.rawData = ByteBuffer.wrap(data);
            this.rawData.rewind();
            this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            this.rawData = null;
            this.header.status = 2;
        }

        return this;
    }

    public void clear() {
        this.rawData = null;
        this.header = null;
    }

    private void reset() {
        this.rawData = null;
        Arrays.fill(this.block, (byte)0);
        this.header = new GifHeader();
        this.blockSize = 0;
    }

    public GifHeader parseHeader() {
        if (this.rawData == null) {
            throw new IllegalStateException("You must call setData() before parseHeader()");
        } else if (this.err()) {
            return this.header;
        } else {
            this.readHeader();
            if (!this.err()) {
                this.readContents();
                if (this.header.frameCount < 0) {
                    this.header.status = 1;
                }
            }

            return this.header;
        }
    }

    private void readContents() {
        boolean done = false;

        while(!done && !this.err()) {
            int code = this.read();
            switch(code) {
                case 0:
                default:
                    this.header.status = 1;
                    break;
                case 33:
                    code = this.read();
                    switch(code) {
                        case 1:
                            this.skip();
                            continue;
                        case 249:
                            this.header.currentFrame = new GifFrame();
                            this.readGraphicControlExt();
                            continue;
                        case 254:
                            this.skip();
                            continue;
                        case 255:
                            this.readBlock();
                            String app = "";

                            for(int i = 0; i < 11; ++i) {
                                app = app + (char)this.block[i];
                            }

                            if (app.equals("NETSCAPE2.0")) {
                                this.readNetscapeExt();
                            } else {
                                this.skip();
                            }
                            continue;
                        default:
                            this.skip();
                            continue;
                    }
                case 44:
                    if (this.header.currentFrame == null) {
                        this.header.currentFrame = new GifFrame();
                    }

                    this.readBitmap();
                    break;
                case 59:
                    done = true;
            }
        }

    }

    private void readGraphicControlExt() {
        this.read();
        int packed = this.read();
        this.header.currentFrame.dispose = (packed & 28) >> 2;
        if (this.header.currentFrame.dispose == 0) {
            this.header.currentFrame.dispose = 1;
        }

        this.header.currentFrame.transparency = (packed & 1) != 0;
        int delayInHundredthsOfASecond = this.readShort();
        if (delayInHundredthsOfASecond < 3) {
            delayInHundredthsOfASecond = 10;
        }

        this.header.currentFrame.delay = delayInHundredthsOfASecond * 10;
        this.header.currentFrame.transIndex = this.read();
        this.read();
    }

    private void readBitmap() {
        this.header.currentFrame.ix = this.readShort();
        this.header.currentFrame.iy = this.readShort();
        this.header.currentFrame.iw = this.readShort();
        this.header.currentFrame.ih = this.readShort();
        int packed = this.read();
        boolean lctFlag = (packed & 128) != 0;
        int lctSize = (int)Math.pow(2.0D, (double)((packed & 7) + 1));
        this.header.currentFrame.interlace = (packed & 64) != 0;
        if (lctFlag) {
            this.header.currentFrame.lct = this.readColorTable(lctSize);
        } else {
            this.header.currentFrame.lct = null;
        }

        this.header.currentFrame.bufferFrameStart = this.rawData.position();
        this.skipImageData();
        if (!this.err()) {
            ++this.header.frameCount;
            this.header.frames.add(this.header.currentFrame);
        }
    }

    private void readNetscapeExt() {
        do {
            this.readBlock();
            if (this.block[0] == 1) {
                int b1 = this.block[1] & 255;
                int b2 = this.block[2] & 255;
                this.header.loopCount = b2 << 8 | b1;
            }
        } while(this.blockSize > 0 && !this.err());

    }

    private void readHeader() {
        String id = "";

        for(int i = 0; i < 6; ++i) {
            id = id + (char)this.read();
        }

        if (!id.startsWith("GIF")) {
            this.header.status = 1;
        } else {
            this.readLSD();
            if (this.header.gctFlag && !this.err()) {
                this.header.gct = this.readColorTable(this.header.gctSize);
                this.header.bgColor = this.header.gct[this.header.bgIndex];
            }

        }
    }

    private void readLSD() {
        this.header.width = this.readShort();
        this.header.height = this.readShort();
        int packed = this.read();
        this.header.gctFlag = (packed & 128) != 0;
        this.header.gctSize = 2 << (packed & 7);
        this.header.bgIndex = this.read();
        this.header.pixelAspect = this.read();
    }

    private int[] readColorTable(int ncolors) {
        int nbytes = 3 * ncolors;
        int[] tab = null;
        byte[] c = new byte[nbytes];

        try {
            this.rawData.get(c);
            tab = new int[256];
            int i = 0;

            int r;
            int g;
            int b;
            for(int var6 = 0; i < ncolors; tab[i++] = -16777216 | r << 16 | g << 8 | b) {
                r = c[var6++] & 255;
                g = c[var6++] & 255;
                b = c[var6++] & 255;
            }
        } catch (BufferUnderflowException var10) {
            if (Log.isLoggable("GifHeaderParser", Log.DEBUG)) {
                Log.d("GifHeaderParser", "Format Error Reading Color Table", var10);
            }

            this.header.status = 1;
        }

        return tab;
    }

    private void skipImageData() {
        this.read();
        this.skip();
    }

    private void skip() {
        int blockSize;
        do {
            blockSize = this.read();
            this.rawData.position(this.rawData.position() + blockSize);
        } while(blockSize > 0);

    }

    private int readBlock() {
        this.blockSize = this.read();
        int n = 0;
        if (this.blockSize > 0) {
            byte count = 0;
//
            try {
                while(n < this.blockSize) {
                    int count_ = this.blockSize - n;
                    this.rawData.get(this.block, n, count_);
                    n += count_;
                }
            } catch (Exception var4) {
                if (Log.isLoggable("GifHeaderParser", Log.DEBUG)) {
                    Log.d("GifHeaderParser", "Error Reading Block n: " + n + " count: " + count + " blockSize: " + this.blockSize, var4);
                }

                this.header.status = 1;
            }
        }

        return n;
    }

    private int read() {
        int curByte = 0;

        try {
            curByte = this.rawData.get() & 255;
        } catch (Exception var3) {
            this.header.status = 1;
        }

        return curByte;
    }

    private int readShort() {
        return this.rawData.getShort();
    }

    private boolean err() {
        return this.header.status != 0;
    }
}
