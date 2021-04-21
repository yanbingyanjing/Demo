package com.bumptech.xchat.load.resource.file;

import com.bumptech.xchat.load.resource.SimpleResource;
import com.bumptech.xchat.load.engine.Resource;

import java.io.File;

/**
 * A simple {@link Resource} that wraps a {@link File}.
 */
public class FileResource extends SimpleResource<File> {
    public FileResource(File file) {
        super(file);
    }
}
