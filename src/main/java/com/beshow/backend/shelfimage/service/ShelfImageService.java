package com.beshow.backend.shelfimage.service;

import com.beshow.backend.domain.shelf.Camera;
import com.beshow.backend.domain.shelf.CameraRepository;
import com.beshow.backend.domain.shelf.Shelf;
import com.beshow.backend.domain.shelf.ShelfRepository;
import com.beshow.backend.domain.shelf.ShelfImage;
import com.beshow.backend.global.exception.BusinessException;
import com.beshow.backend.global.exception.ErrorCode;
import com.beshow.backend.global.service.S3UrlService;
import com.beshow.backend.api.s3.service.AWSS3Service;
import com.beshow.backend.shelfimage.dto.ShelfImageUploadResponse;
import com.beshow.backend.domain.shelf.ShelfImageRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ShelfImageService {

    private static final String SHELF_IMAGE_PREFIX = "shelf/";

    private final CameraRepository cameraRepository;
    private final ShelfImageRepository shelfImageRepository;
    private final AWSS3Service awsS3Service;
    private final S3UrlService s3UrlService;

    public ShelfImageUploadResponse uploadShelfImage(
            Long cameraId,
            MultipartFile image,
            LocalDateTime capturedAt
    ) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미지 파일이 비어 있습니다.");
        }

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAMERA_NOT_FOUND));

        Long shelfId = camera.getShelf().getShelfId();

        ImageSize imageSize = readImageSize(image);

        LocalDateTime actualCapturedAt = capturedAt == null
                ? LocalDateTime.now()
                : capturedAt;

        String imageS3Key = createShelfImageKey(
                shelfId,
                cameraId,
                actualCapturedAt,
                image.getOriginalFilename()
        );

        awsS3Service.uploadFile(image, imageS3Key);

        ShelfImage shelfImage = ShelfImage.create(
                camera,
                imageS3Key,
                imageSize.width(),
                imageSize.height(),
                actualCapturedAt
        );

        ShelfImage savedShelfImage = shelfImageRepository.save(shelfImage);

        return new ShelfImageUploadResponse(
                savedShelfImage.getShelfImageId(),
                shelfId,
                camera.getCameraId(),
                savedShelfImage.getImageS3Key(),
                s3UrlService.createPublicUrl(savedShelfImage.getImageS3Key()),
                savedShelfImage.getImageWidth(),
                savedShelfImage.getImageHeight(),
                savedShelfImage.getCapturedAt()
        );
    }

    private String createShelfImageKey(
            Long shelfId,
            Long cameraId,
            LocalDateTime capturedAt,
            String originalFilename
    ) {
        String extension = extractExtension(originalFilename);
        String timestamp = capturedAt.toString()
                .replace(":", "")
                .replace("-", "")
                .replace(".", "");

        return SHELF_IMAGE_PREFIX
                + "shelf-" + shelfId
                + "/camera-" + cameraId
                + "/" + timestamp
                + extension;
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }

        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private ImageSize readImageSize(MultipartFile image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

            if (bufferedImage == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미지 파일 형식이 올바르지 않습니다.");
            }

            return new ImageSize(bufferedImage.getWidth(), bufferedImage.getHeight());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미지 크기를 읽을 수 없습니다.");
        }
    } 
    private record ImageSize(Integer width, Integer height) {
    }
}