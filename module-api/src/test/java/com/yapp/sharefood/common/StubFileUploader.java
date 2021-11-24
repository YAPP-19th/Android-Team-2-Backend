package com.yapp.sharefood.common;

import com.yapp.sharefood.external.s3.StorageUploader;
import org.springframework.web.multipart.MultipartFile;

public class StubFileUploader implements StorageUploader {
    public static final String UPLOAD_FILE_URL = "url";

    @Override
    public String upload(String s3DirPath, MultipartFile file) {
        return UPLOAD_FILE_URL;
    }

    @Override
    public void delete(String s3DirPath, String fileName) {

    }
}
