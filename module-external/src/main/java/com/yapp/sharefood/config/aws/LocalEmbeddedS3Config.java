package com.yapp.sharefood.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Profile({"local", "test"})
@Configuration
public class LocalEmbeddedS3Config {
    private final String region;

    private final String bucket;

    private int port;

    public LocalEmbeddedS3Config(
            @Value("${cloud.aws.region.static}") String region,
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.s3.mock.port}") int port
    ) {
        this.region = region;
        this.bucket = bucket;
        this.port = port;
    }

    @Bean
    public S3Mock s3Mock() throws IOException {
        port = AwsMockUtils.isRunningPort(port) ? AwsMockUtils.findAvailableRandomPort() : port;
        return new S3Mock.Builder()
                .withPort(port)
                .withInMemoryBackend()
                .build();
    }

    @PostConstruct
    public void startS3Mock() throws IOException {
        this.s3Mock().start();
        log.info("인메모리 S3 Mock 서버가 시작됩니다. port: {}", port);
    }

    @PreDestroy
    public void destroyS3Mock() throws IOException {
        this.s3Mock().shutdown();
        log.info("인메모리 S3 Mock 서버가 종료됩니다. port: {}", port);
    }

    @Bean
    @Primary
    public AmazonS3 amazonS3() {
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(getUri(), region);
        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        client.createBucket(bucket);
        return client;
    }

    private String getUri() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .build()
                .toUriString();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AwsMockUtils {

        private static final String OS = System.getProperty("os.name").toLowerCase();

        public static boolean isRunningPort(int port) throws IOException {
            return isRunning(executeGrepProcessCommand(port));
        }

        public static int findAvailableRandomPort() throws IOException {
            for (int port = 10000; port <= 65535; port++) {
                Process process = executeGrepProcessCommand(port);
                if (!isRunning(process)) {
                    return port;
                }
            }
            throw new IllegalArgumentException("사용가능한 포트를 찾을 수 없습니다. (10000 ~ 65535)");
        }

        private static Process executeGrepProcessCommand(int port) throws IOException {
            // 윈도우일 경우
            if (isWindows()) {
                String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
                String[] shell = {"cmd.exe", "/y", "/c", command};
                return Runtime.getRuntime().exec(shell);
            }
            String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
            String[] shell = {"/bin/sh", "-c", command};
            return Runtime.getRuntime().exec(shell);
        }

        private static boolean isWindows() {
            return OS.contains("win");
        }

        private static boolean isRunning(Process process) {
            String line;
            StringBuilder pidInfo = new StringBuilder();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = input.readLine()) != null) {
                    pidInfo.append(line);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("사용가능한 포트를 찾는 중 에러가 발생하였습니다.");
            }
            return StringUtils.hasLength(pidInfo.toString());
        }
    }
}
