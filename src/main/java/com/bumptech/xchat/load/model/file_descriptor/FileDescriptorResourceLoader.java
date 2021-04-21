package com.bumptech.xchat.load.model.file_descriptor;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.bumptech.xchat.Glide;
import com.bumptech.xchat.load.model.GenericLoaderFactory;
import com.bumptech.xchat.load.model.ModelLoader;
import com.bumptech.xchat.load.model.ModelLoaderFactory;
import com.bumptech.xchat.load.model.ResourceLoader;
import com.bumptech.xchat.Glide;

/**
 * A {@link ModelLoader} For translating android resource id models into {@link ParcelFileDescriptor} data.
 */
public class FileDescriptorResourceLoader extends ResourceLoader<ParcelFileDescriptor>
        implements FileDescriptorModelLoader<Integer> {

    /**
     * The default factory for {@link FileDescriptorResourceLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<Integer, ParcelFileDescriptor> {

        @Override
        public ModelLoader<Integer, ParcelFileDescriptor> build(Context context, GenericLoaderFactory factories) {
            return new FileDescriptorResourceLoader(context, factories.buildModelLoader(Uri.class,
                    ParcelFileDescriptor.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }

    public FileDescriptorResourceLoader(Context context) {
        this(context, Glide.buildFileDescriptorModelLoader(Uri.class, context));
    }

    public FileDescriptorResourceLoader(Context context, ModelLoader<Uri, ParcelFileDescriptor> uriLoader) {
        super(context, uriLoader);
    }
}
