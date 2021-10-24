package com.yapp.sharefood.external.s3;

import java.io.File;

public interface StorageUploader {
    String upload(String s3DirPath, File file);

    void delete(String s3DirPath, String fileName);
}
