package com.b110.jjeonchongmu.global.exception;

public enum ErrorCode {
    GATHERING_NOT_FOUND, USER_NOT_FOUND,
    ;
    // Other error codes...
    public static final ErrorCode INVALID_PASSWORD = ErrorCode.valueOf("잘못된 비밀번호입니다");
    public static final ErrorCode DUPLICATE_EMAIL = ErrorCode.valueOf("이미 사용중인 이메일입니다");

    public static final ErrorCode INVALID_TOKEN = ErrorCode.valueOf("유효하지 않은 토큰입니다");

    public static final ErrorCode INVALID_MANAGER = ErrorCode.valueOf("모임 관리자가 아닙니다");
}