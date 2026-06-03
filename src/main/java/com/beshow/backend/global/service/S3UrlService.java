package com.beshow.backend.global.service;

import com.beshow.backend.global.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3UrlService {

    private final S3Properties s3Properties;

    public String createPublicUrl(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return null;
        }

        return "https://"
                + s3Properties.bucket()
                + ".s3."
                + s3Properties.region()
                + ".amazonaws.com/"
                + s3Key;
    }

    public String createProductImageUrl(String productImageS3Key) {
        return createPublicUrl(productImageS3Key);
    }

    public String createShelfImageUrl(String shelfImageS3Key) {
        return createPublicUrl(shelfImageS3Key);
    }
}