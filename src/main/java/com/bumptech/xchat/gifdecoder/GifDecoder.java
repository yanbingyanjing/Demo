//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bumptech.xchat.gifdecoder;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

public class GifDecoder {
    private static final String TAG = GifDecoder.class.getSimpleName();
    public static final int STATUS_OK = 0;
    public static final int STATUS_FORMAT_ERROR = 1;
    public static final int STATUS_OPEN_ERROR = 2;
    public static final int STATUS_PARTIAL_DECODE = 3;
    private static final int MAX_STACK_SIZE = 4096;
    private static final int DISPOSAL_UNSPECIFIED = 0;
    private static final int DISPOSAL_NONE = 1;
    private static final int DISPOSAL_BACKGROUND = 2;
    private static final int DISPOSAL_PREVIOUS = 3;
    private static final int NULL_CODE = -1;
    private static final int INITIAL_FRAME_POINTER = -1;
    private static final Config BITMAP_CONFIG;
    private int[] act;
    private ByteBuffer rawData;
    private final byte[] block = new byte[256];
    private GifHeaderParser parser;
    private short[] prefix;
    private byte[] suffix;
    private byte[] pixelStack;
    private byte[] mainPixels;
    private int[] mainScratch;
    private int framePointer;
    private byte[] data;
    private GifHeader header;
    private GifDecoder.BitmapProvider bitmapProvider;
    private Bitmap previousImage;
    private boolean savePrevious;
    private int status;

    public GifDecoder(GifDecoder.BitmapProvider provider) {
        this.bitmapProvider = provider;
        this.header = new GifHeader();
    }

    public int getWidth() {
        return this.header.width;
    }

    public int getHeight() {
        return this.header.height;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getStatus() {
        return this.status;
    }

    public void advance() {
        this.framePointer = (this.framePointer + 1) % this.header.frameCount;
    }

    public int getDelay(int n) {
        int delay = -1;
        if (n >= 0 && n < this.header.frameCount) {
            delay = ((GifFrame)this.header.frames.get(n)).delay;
        }

        return delay;
    }

    public int getNextDelay() {
        return this.header.frameCount > 0 && this.framePointer >= 0 ? this.getDelay(this.framePointer) : -1;
    }

    public int getFrameCount() {
        return this.header.frameCount;
    }

    public int getCurrentFrameIndex() {
        return this.framePointer;
    }

    public void resetFrameIndex() {
        this.framePointer = -1;
    }

    public int getLoopCount() {
        return this.header.loopCount;
    }

    public synchronized Bitmap getNextFrame() {
        if (this.header.frameCount <= 0 || this.framePointer < 0) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "unable to decode frame, frameCount=" + this.header.frameCount + " framePointer=" + this.framePointer);
            }

            this.status = 1;
        }

        if (this.status != 1 && this.status != 2) {
            this.status = 0;
            GifFrame currentFrame = (GifFrame)this.header.frames.get(this.framePointer);
            GifFrame previousFrame = null;
            int previousIndex = this.framePointer - 1;
            if (previousIndex >= 0) {
                previousFrame = (GifFrame)this.header.frames.get(previousIndex);
            }

            if (currentFrame.lct == null) {
                this.act = this.header.gct;
            } else {
                this.act = currentFrame.lct;
                if (this.header.bgIndex == currentFrame.transIndex) {
                    this.header.bgColor = 0;
                }
            }

            int save = 0;
            if (currentFrame.transparency) {
                save = this.act[currentFrame.transIndex];
                this.act[currentFrame.transIndex] = 0;
            }

            if (this.act == null) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "No Valid Color Table");
                }

                this.status = 1;
                return null;
            } else {
                Bitmap result = this.setPixels(currentFrame, previousFrame);
                if (currentFrame.transparency) {
                    this.act[currentFrame.transIndex] = save;
                }

                return result;
            }
        } else {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Unable to decode frame, status=" + this.status);
            }

            return null;
        }
    }

    public int read(InputStream is, int contentLength) {
        if (is != null) {
            try {
                int capacity = contentLength > 0 ? contentLength + 4096 : 16384;
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(capacity);
                byte[] data = new byte[16384];

                int nRead;
                while((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                this.read(buffer.toByteArray());
            } catch (IOException var8) {
                Log.w(TAG, "Error reading data from stream", var8);
            }
        } else {
            this.status = 2;
        }

        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException var7) {
            Log.w(TAG, "Error closing stream", var7);
        }

        return this.status;
    }

    public void clear() {
        this.header = null;
        this.data = null;
        this.mainPixels = null;
        this.mainScratch = null;
        if (this.previousImage != null) {
            this.bitmapProvider.release(this.previousImage);
        }

        this.previousImage = null;
        this.rawData = null;
    }

    public void setData(GifHeader header, byte[] data) {
        this.header = header;
        this.data = data;
        this.status = 0;
        this.framePointer = -1;
        this.rawData = ByteBuffer.wrap(data);
        this.rawData.rewind();
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        this.savePrevious = false;
        Iterator i$ = header.frames.iterator();

        while(i$.hasNext()) {
            GifFrame frame = (GifFrame)i$.next();
            if (frame.dispose == 3) {
                this.savePrevious = true;
                break;
            }
        }

        this.mainPixels = new byte[header.width * header.height];
        this.mainScratch = new int[header.width * header.height];
    }

    private GifHeaderParser getHeaderParser() {
        if (this.parser == null) {
            this.parser = new GifHeaderParser();
        }

        return this.parser;
    }

    public int read(byte[] data) {
        this.data = data;
        this.header = this.getHeaderParser().setData(data).parseHeader();
        if (data != null) {
            this.rawData = ByteBuffer.wrap(data);
            this.rawData.rewind();
            this.rawData.order(ByteOrder.LITTLE_ENDIAN);
            this.mainPixels = new byte[this.header.width * this.header.height];
            this.mainScratch = new int[this.header.width * this.header.height];
            this.savePrevious = false;
            Iterator i$ = this.header.frames.iterator();

            while(i$.hasNext()) {
                GifFrame frame = (GifFrame)i$.next();
                if (frame.dispose == 3) {
                    this.savePrevious = true;
                    break;
                }
            }
        }

        return this.status;
    }

    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame) {
        int width = this.header.width;
        int height = this.header.height;
        int[] dest = this.mainScratch;
        int pass;
        if (previousFrame != null && previousFrame.dispose > 0) {
            if (previousFrame.dispose == 2) {
                pass = 0;
                if (!currentFrame.transparency) {
                    pass = this.header.bgColor;
                }

                Arrays.fill(dest, pass);
            } else if (previousFrame.dispose == 3 && this.previousImage != null) {
                this.previousImage.getPixels(dest, 0, width, 0, 0, width, height);
            }
        }

        this.decodeBitmapData(currentFrame);
        pass = 1;
        int inc = 8;
        int iline = 0;

        for(int i = 0; i < currentFrame.ih; ++i) {
            int line = i;
            if (currentFrame.interlace) {
                if (iline >= currentFrame.ih) {
                    ++pass;
                    switch(pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                    }
                }

                line = iline;
                iline += inc;
            }

            line += currentFrame.iy;
            if (line < this.header.height) {
                int k = line * this.header.width;
                int dx = k + currentFrame.ix;
                int dlim = dx + currentFrame.iw;
                if (k + this.header.width < dlim) {
                    dlim = k + this.header.width;
                }

                for(int var14 = i * currentFrame.iw; dx < dlim; ++dx) {
                    int index = this.mainPixels[var14++] & 255;
                    int c = this.act[index];
                    if (c != 0) {
                        dest[dx] = c;
                    }
                }
            }
        }

        if (this.savePrevious && (currentFrame.dispose == 0 || currentFrame.dispose == 1)) {
            if (this.previousImage == null) {
                this.previousImage = this.getNextBitmap();
            }

            this.previousImage.setPixels(dest, 0, width, 0, 0, width, height);
        }

        Bitmap result = this.getNextBitmap();
        result.setPixels(dest, 0, width, 0, 0, width, height);
        return result;
    }

    private void decodeBitmapData(GifFrame frame) {
        if (frame != null) {
            this.rawData.position(frame.bufferFrameStart);
        }

        int npix = frame == null ? this.header.width * this.header.height : frame.iw * frame.ih;
        if (this.mainPixels == null || this.mainPixels.length < npix) {
            this.mainPixels = new byte[npix];
        }

        if (this.prefix == null) {
            this.prefix = new short[4096];
        }

        if (this.suffix == null) {
            this.suffix = new byte[4096];
        }

        if (this.pixelStack == null) {
            this.pixelStack = new byte[4097];
        }

        int dataSize = this.read();
        int clear = 1 << dataSize;
        int endOfInformation = clear + 1;
        int available = clear + 2;
        int oldCode = -1;
        int codeSize = dataSize + 1;
        int codeMask = (1 << codeSize) - 1;

        int code;
        for(code = 0; code < clear; ++code) {
            this.prefix[code] = 0;
            this.suffix[code] = (byte)code;
        }

        int bi = 0;
        int pi = 0;
        int top = 0;
        int first = 0;
        int count = 0;
        int bits = 0;
        int datum = 0;
        int i = 0;

        label102:
        while(i < npix) {
            if (count == 0) {
                count = this.readBlock();
                if (count <= 0) {
                    this.status = 3;
                    break;
                }

                bi = 0;
            }

            datum += (this.block[bi] & 255) << bits;
            bits += 8;
            ++bi;
            --count;

            while(true) {
                while(true) {
                    if (bits < codeSize) {
                        continue label102;
                    }

                    code = datum & codeMask;
                    datum >>= codeSize;
                    bits -= codeSize;
                    if (code == clear) {
                        codeSize = dataSize + 1;
                        codeMask = (1 << codeSize) - 1;
                        available = clear + 2;
                        oldCode = -1;
                    } else {
                        if (code > available) {
                            this.status = 3;
                            continue label102;
                        }

                        if (code == endOfInformation) {
                            continue label102;
                        }

                        if (oldCode == -1) {
                            this.pixelStack[top++] = this.suffix[code];
                            oldCode = code;
                            first = code;
                        } else {
                            int inCode = code;
                            if (code >= available) {
                                this.pixelStack[top++] = (byte)first;
                                code = oldCode;
                            }

                            while(code >= clear) {
                                this.pixelStack[top++] = this.suffix[code];
                                code = this.prefix[code];
                            }

                            first = this.suffix[code] & 255;
                            this.pixelStack[top++] = (byte)first;
                            if (available < 4096) {
                                this.prefix[available] = (short)oldCode;
                                this.suffix[available] = (byte)first;
                                ++available;
                                if ((available & codeMask) == 0 && available < 4096) {
                                    ++codeSize;
                                    codeMask += available;
                                }
                            }

                            for(oldCode = inCode; top > 0; ++i) {
                                --top;
                                this.mainPixels[pi++] = this.pixelStack[top];
                            }
                        }
                    }
                }
            }
        }

        for(i = pi; i < npix; ++i) {
            this.mainPixels[i] = 0;
        }

    }

    private int read() {
        int curByte = 0;

        try {
            curByte = this.rawData.get() & 255;
        } catch (Exception var3) {
            this.status = 1;
        }

        return curByte;
    }

    private int readBlock() {
        int blockSize = this.read();
        int n = 0;
        if (blockSize > 0) {
            try {
                while(n < blockSize) {
                    int count = blockSize - n;
                    this.rawData.get(this.block, n, count);
                    n += count;
                }
            } catch (Exception var4) {
                Log.w(TAG, "Error Reading Block", var4);
                this.status = 1;
            }
        }

        return n;
    }

    private Bitmap getNextBitmap() {
        Bitmap result = this.bitmapProvider.obtain(this.header.width, this.header.height, BITMAP_CONFIG);
        if (result == null) {
            result = Bitmap.createBitmap(this.header.width, this.header.height, BITMAP_CONFIG);
        }

        setAlpha(result);
        return result;
    }

    @TargetApi(12)
    private static void setAlpha(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(true);
        }

    }

    static {
        BITMAP_CONFIG = Config.ARGB_8888;
    }

    public interface BitmapProvider {
        Bitmap obtain(int var1, int var2, Config var3);

        void release(Bitmap var1);
    }
}
