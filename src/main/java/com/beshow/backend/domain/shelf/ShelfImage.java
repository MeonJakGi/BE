package com.beshow.backend.domain.shelf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shelf_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShelfImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelf_image_id")
    private Long shelfImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @Column(name = "image_s3_key", nullable = false, length = 500)
    private String imageS3Key;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false, length = 30)
    private AnalysisStatus analysisStatus;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "analysis_message", length = 1000)
    private String analysisMessage;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "analysis_requested_at")
    private LocalDateTime analysisRequestedAt;

    @Column(name = "analysis_completed_at")
    private LocalDateTime analysisCompletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ShelfImage create(
            Camera camera,
            String imageS3Key,
            Integer imageWidth,
            Integer imageHeight,
            LocalDateTime capturedAt
    ) {
        ShelfImage shelfImage = new ShelfImage();
        shelfImage.camera = camera;
        shelfImage.imageS3Key = imageS3Key;
        shelfImage.imageWidth = imageWidth;
        shelfImage.imageHeight = imageHeight;
        shelfImage.capturedAt = capturedAt;
        shelfImage.analysisStatus = AnalysisStatus.PENDING;
        shelfImage.retryCount = 0;
        shelfImage.createdAt = LocalDateTime.now();
        return shelfImage;
    }

    public void markImageNotFound(String message) {
        this.analysisStatus = AnalysisStatus.IMAGE_NOT_FOUND;
        this.retryCount += 1;
        this.analysisMessage = message;
        this.lastCheckedAt = LocalDateTime.now();
    }

    public void markRequested(String message) {
        this.analysisStatus = AnalysisStatus.REQUESTED;
        this.analysisMessage = message;
        this.lastCheckedAt = LocalDateTime.now();
        this.analysisRequestedAt = LocalDateTime.now();
    }

    public void markFailed(String message) {
        this.analysisStatus = AnalysisStatus.FAILED;
        this.analysisMessage = message;
        this.lastCheckedAt = LocalDateTime.now();
    }

    public void markDone(String message) {
        this.analysisStatus = AnalysisStatus.COMPLETE;
        this.analysisMessage = message;
        this.lastCheckedAt = LocalDateTime.now();
        this.analysisCompletedAt = LocalDateTime.now();
    }

    public void resetPending(String message) {
        this.analysisStatus = AnalysisStatus.PENDING;
        this.analysisMessage = message;
        this.lastCheckedAt = LocalDateTime.now();
    }
}