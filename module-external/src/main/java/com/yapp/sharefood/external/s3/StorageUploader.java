package com.yapp.sharefood.external.s3;

import org.springframework.web.multipart.MultipartFile;

public interface StorageUploader {
    String upload(String s3DirPath, MultipartFile file);

    void delete(String s3DirPath, String fileName);
}
