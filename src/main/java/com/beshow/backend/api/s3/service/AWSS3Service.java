package com.beshow.backend.api.s3.service;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.beshow.backend.global.exception.BusinessException;
import com.beshow.backend.global.exception.ErrorCode;
import com.beshow.backend.api.s3.dto.PresignedUploadUrlRequest;
import com.beshow.backend.api.s3.dto.PresignedUploadUrlResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AWSS3Service {

	@Value("${aws.s3.access-key}")
	private String accessKey;
	@Value("${aws.s3.secret-key}")
	private String secretKey;
	@Value("${aws.s3.bucket}")
	private String bucket;
	@Value("${aws.s3.region}")
	private String region;

	private final S3Client s3Client;

	public PresignedUploadUrlResponse createPresignedUploadUrl(PresignedUploadUrlRequest request) {
		String fileId = createFileId();
		String prefix = request.folderName();
		String fileName = request.originalFilename();

		String filePath = request.folderName().isEmpty()
				? fileId + "." + getExt(fileName)
				: createPath(prefix, fileName, fileId);

		// Presigner 생성
		try (S3Presigner presigner = S3Presigner.builder()
				.region(Region.of(region))
				.credentialsProvider(
						StaticCredentialsProvider.create(
								AwsBasicCredentials.create(accessKey, secretKey)))
				.build()) {

			// 업로드 요청 정의
			PutObjectRequest objectRequest = PutObjectRequest.builder()
					.bucket(bucket)
					.key(filePath)
					.build();

			// Presign 요청 정의 (유효기간 2분)
			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(2))
					.putObjectRequest(objectRequest)
					.build();

			// Presigned URL 생성
			PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
			URL url = presignedRequest.url();

			return new PresignedUploadUrlResponse(prefix, filePath, url.toString());
		}
	}

	private String createFileId() {
		return UUID.randomUUID().toString();
	}

	private String getExt(String fileName) {
		String[] fileSplit = fileName.split("\\.");
		String ext = fileSplit[fileSplit.length - 1];
		return ext;
	}

	private String createPath(String prefix, String fileName, String fileId) {
		String ext = getExt(fileName);
		return String.format("%s/%s", prefix, fileId + "." + ext);
	}

	public String uploadFile(MultipartFile file, String s3Key) {
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucket)
					.key(s3Key)
					.contentType(file.getContentType())
					.build();

			s3Client.putObject(
					putObjectRequest,
					RequestBody.fromBytes(file.getBytes())
			);

			return s3Key;
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "S3 파일 업로드에 실패했습니다.");
		}
	}
}