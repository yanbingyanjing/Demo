package com.bumptech.xchat.load.model.file_descriptor;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.model.FileLoader;
import com.bumptech.xchat.load.model.GenericLoaderFactory;
import com.bumptech.xchat.load.model.ModelLoader;
import com.bumptech.xchat.load.model.ModelLoaderFactory;
import com.bumptech.xchat.Glide;

import java.io.File;

/**
 * A {@link ModelLoader} For translating {@link File} models into {@link ParcelFileDescriptor} data.
 */
public class FileDescriptorFileLoader extends FileLoader<ParcelFileDescriptor>
        implements FileDescriptorModelLoader<File> {

    /**
     * The default {@link ModelLoaderFactory} for
     * {@link FileDescriptorFileLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<File, ParcelFileDescriptor> {
        @Override
        public ModelLoader<File, ParcelFileDescriptor> build(Context context, GenericLoaderFactory factories) {
            return new FileDescriptorFileLoader(factories.buildModelLoader(Uri.class, ParcelFileDescriptor.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }

    public FileDescriptorFileLoader(Context context) {
        this(Glide.buildFileDescriptorModelLoader(Uri.class, context));
    }

    public FileDescriptorFileLoader(ModelLoader<Uri, ParcelFileDescriptor> uriLoader) {
        super(uriLoader);
    }
}
