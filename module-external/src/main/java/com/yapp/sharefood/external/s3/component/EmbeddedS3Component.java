package com.yapp.sharefood.external.s3.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Profile({"local"})
public class EmbeddedS3Component implements AwsS3Component {
    private String bucket;
    private String cloudFrontUrl;

    public EmbeddedS3Component(@Value("${cloud.aws.s3.bucket}") String bucket,
                               @Value("${cloud.aws.s3.mock.port}") int port) {
        this.bucket = bucket;
        this.cloudFrontUrl = "http://localhost:" + port + "/" + bucket;
    }
}
