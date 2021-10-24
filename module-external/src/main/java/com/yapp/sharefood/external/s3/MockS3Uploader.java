package com.yapp.sharefood.external.s3;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Profile({"dev"})
public class MockS3Uploader implements StorageUploader {
    @Override
    public String upload(String s3DirPath, File file) {
        return null;
    }

    @Override
    public void delete(String s3DirPath, String fileName) {

    }
}
