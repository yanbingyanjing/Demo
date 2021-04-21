package com.yjfshop123.live.server.utils.imageloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public interface ImageDownloader {
    InputStream getStream(String var1, Object var2) throws IOException;

    public static enum Scheme {
        HTTP("http"),
        HTTPS("https"),
        FILE("file"),
        CONTENT("content"),
        ASSETS("assets"),
        DRAWABLE("drawable"),
        AVATAR("avatar"),
        UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        private Scheme(String scheme) {
            this.scheme = scheme;
            this.uriPrefix = scheme + "://";
        }

        public static ImageDownloader.Scheme ofUri(String uri) {
            if (uri != null) {
                ImageDownloader.Scheme[] var1 = values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    ImageDownloader.Scheme s = var1[var3];
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }

            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(this.uriPrefix);
        }

        public String wrap(String path) {
            return this.uriPrefix + path;
        }

        public String crop(String uri) {
            if (!this.belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, this.scheme));
            } else {
                return uri.substring(this.uriPrefix.length());
            }
        }
    }
}
