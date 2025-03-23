package com.b110.jjeonchongmu.global.common;

import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDetailResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringListResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 응답 공통 형식
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public ApiResponse(int statusCode, String message) {
        this.success = (statusCode >= 200 && statusCode < 300);
        this.message = message;
    }

    public ApiResponse(int statusCode, String message, T data) {
        this.success = (statusCode >= 200 && statusCode < 300);
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "성공", data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "성공");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, message);
    }
}