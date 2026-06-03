package com.beshow.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid input value."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Duplicate resource."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),
    SHELF_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Shelf image not found."),
    CAMERA_NOT_FOUND(HttpStatus.NOT_FOUND, "Camera not found."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
