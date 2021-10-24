package com.yapp.sharefood.external.s3;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile({"local"})
public class MockS3Uploader implements StorageUploader {
    @Override
    public String upload(String s3DirPath, MultipartFile file) {
        return null;
    }

    @Override
    public void delete(String s3DirPath, String fileName) {

    }
}
