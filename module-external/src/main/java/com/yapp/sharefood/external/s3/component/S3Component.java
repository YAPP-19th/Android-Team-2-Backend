package com.yapp.sharefood.external.s3.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class S3Component {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.cloud-front-url}")
    private String cloudFrontUrl;
}