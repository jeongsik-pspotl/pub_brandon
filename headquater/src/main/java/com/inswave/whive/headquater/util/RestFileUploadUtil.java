package com.inswave.whive.headquater.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Component
public class RestFileUploadUtil {

    public FilenameAwareInputStreamResource generateFilenameAwareByteArrayResource(MultipartFile agreement) {
        try {
            return new FilenameAwareInputStreamResource(agreement.getInputStream(), agreement.getSize(), String.format("%s", agreement.getOriginalFilename()));
        } catch (Exception e) {
            log.error("Occur exception", e);
            return null;
        }
    }

    private static class FilenameAwareInputStreamResource extends InputStreamResource {
        private final String filename;
        private final long contentLength;

        public FilenameAwareInputStreamResource(InputStream inputStream, long contentLength, String filename) {
            super(inputStream);
            this.filename = filename;
            this.contentLength = contentLength;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }

}
