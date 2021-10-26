package com.yapp.sharefood.external.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.yapp.sharefood.common.exception.file.FileUploadException;
import com.yapp.sharefood.common.utils.FileUtils;
import com.yapp.sharefood.external.s3.component.AwsS3Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Uploader implements StorageUploader {
    private final AmazonS3 amazonS3;
    private final AwsS3Component component;

    @Override
    public String upload(String s3DirPath, MultipartFile file) {
        try {
            final String saveFileName = FileUtils.createSaveFileName(s3DirPath, file.getOriginalFilename());
            ObjectMetadata saveMetadata = createSaveObjectMetadata(file);
            amazonS3.putObject(component.getBucket(), saveFileName, file.getInputStream(), saveMetadata);

            return component.getCloudFrontUrl() + "/" + saveFileName;
        } catch (Exception e) {
            throw new FileUploadException();
        }
    }

    private ObjectMetadata createSaveObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    @Override
    public void delete(String s3DirPath, String fileName) {
        try {
            log.debug("S3에서 삭제할 이미지 경로: {}", s3DirPath + fileName);
            final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(component.getBucket(), s3DirPath + fileName);
            amazonS3.deleteObject(deleteObjectRequest);
            log.debug("S3의 {} 파일 삭제 성공", s3DirPath + fileName);
        } catch (AmazonServiceException e) {
            log.info("S3의 {} 파일 삭제 실패", s3DirPath + fileName);
        }
    }
}
