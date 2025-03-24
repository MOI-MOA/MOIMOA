package com.b110.jjeonchongmu.global.exception;

public enum ErrorCode {
    GATHERING_NOT_FOUND, USER_NOT_FOUND,
    SCHEDULE_NOT_FOUND;
    // Other error codes...
    public static final ErrorCode INVALID_PASSWORD = ErrorCode.valueOf("잘못된 비밀번호입니다");
    public static final ErrorCode DUPLICATE_EMAIL = ErrorCode.valueOf("이미 사용중인 이메일입니다");
    public static final ErrorCode INVALID_TOKEN = ErrorCode.valueOf("유효하지 않은 토큰입니다");
    public static final ErrorCode INVALID_MANAGER = ErrorCode.valueOf("모임 관리자가 아닙니다");
    public static final ErrorCode SCHEDULE_BUDGET_NOT_SET = ErrorCode.valueOf("일정 예산이 설정되지 않았습니다");
    public static final ErrorCode ALREADY_SCHEDULE_MEMBER = ErrorCode.valueOf("이미 일정 멤버입니다");
    public static final ErrorCode SCHEDULE_MEMBER_NOT_FOUND = ErrorCode.valueOf("일정 멤버를 찾을 수 없습니다");

//    public static final ErrorCode SCHEDULE_NOT_FOUND = ErrorCode.valueOf("일정을 찾을 수 없습니다");

}