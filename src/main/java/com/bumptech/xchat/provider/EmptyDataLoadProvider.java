package com.bumptech.xchat.provider;

import com.bumptech.xchat.load.Encoder;
import com.bumptech.xchat.load.ResourceDecoder;
import com.bumptech.xchat.load.ResourceEncoder;

import java.io.File;

/**
 * A {@link DataLoadProvider} that returns {@code null} for every class.
 *
 * @param <T> unused data type.
 * @param <Z> unused resource type.
 */
public class EmptyDataLoadProvider<T, Z> implements DataLoadProvider<T, Z> {
    private static final DataLoadProvider<?, ?> EMPTY_DATA_LOAD_PROVIDER = new EmptyDataLoadProvider<Object, Object>();

    @SuppressWarnings("unchecked")
    public static <T, Z> DataLoadProvider<T, Z> get() {
        return (DataLoadProvider<T, Z>) EMPTY_DATA_LOAD_PROVIDER;
    }

    @Override
    public ResourceDecoder<File, Z> getCacheDecoder() {
        return null;
    }

    @Override
    public ResourceDecoder<T, Z> getSourceDecoder() {
        return null;
    }

    @Override
    public Encoder<T> getSourceEncoder() {
        return null;
    }

    @Override
    public ResourceEncoder<Z> getEncoder() {
        return null;
    }
}
