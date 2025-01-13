package com.pspotl.sidebranden.manager.util;

import org.springframework.core.io.ByteArrayResource;

public class FileNameAwareByteArrayResource extends ByteArrayResource {

    private String fileName;

    public FileNameAwareByteArrayResource(String fileName, byte[] byteArray, String description) {
        super(byteArray, description);
        this.fileName = fileName;
    }

    @Override
    public String getFilename() {
        return fileName;
    }
}
