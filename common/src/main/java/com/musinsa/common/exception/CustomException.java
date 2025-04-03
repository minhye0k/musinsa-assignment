package com.musinsa.common.exception;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomException extends RuntimeException {
    private static int NOT_FOUND = 404;
    private static int BAD_REQUEST = 400;

    private String message;
    private int statusCode;

    public static CustomException notFound(String message) {
        return CustomException.builder()
                .message(message)
                .statusCode(NOT_FOUND)
                .build();
    }

    public static CustomException badRequest(String message) {
        return CustomException.builder()
                .message(message)
                .statusCode(BAD_REQUEST)
                .build();
    }
}
