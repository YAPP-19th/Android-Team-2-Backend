package com.yapp.sharefood.external.s3.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Profile({"dev", "prod"})
public class S3Component implements AwsS3Component {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.cloud-front-url}")
    private String cloudFrontUrl;
}