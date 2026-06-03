package com.beshow.backend.domain.shelf;

public enum AnalysisStatus {
    PENDING,        // 분석 대기
    IMAGE_NOT_FOUND,// 이미지 접근 실패
    REQUESTED,      // AI 서버 호출 완료
    FAILED,          // 최종 실패
    COMPLETE              // 분석 완료
}