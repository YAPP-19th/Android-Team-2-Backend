package com.yapp.sharefood.external.s3;

import com.yapp.sharefood.common.exception.file.FileUploadException;
import com.yapp.sharefood.common.utils.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
@Profile({"local"})
public class MockS3Uploader implements StorageUploader {
    @Override
    public String upload(String s3DirPath, MultipartFile file) {
        String path = System.getProperty("user.dir");
        final String saveFileName = path + "/" + FileUtils.createSaveFileName(s3DirPath, file.getOriginalFilename());
        try {
            file.transferTo(new File(saveFileName));
            return saveFileName;
        } catch (Exception e) {
            throw new FileUploadException();
        }
    }

    @Override
    public void delete(String s3DirPath, String fileName) {

    }
}
