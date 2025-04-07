package com.b110.jjeonchongmu.global.exception;

public enum ErrorCode {
    GATHERING_NOT_FOUND("모임을 찾을 수 없습니다."),
    ALREADY_GATHERING_MEMBER("이미 해당 모임에 가입된 회원입니다."),
    UNAUTHORIZED("인가되지 않은 사용자 : 접근권한이 없습니다."),
    DUPLICATE_GATHERING_NAME("이미 존재하는 모임 이름입니다."),
    GATHERING_MEMBER_LIMIT("모임 회원 수가 초과되었습니다."),
    GATHERING_ACCOUNT_NOT_FOUND("모임 계좌를 찾을 수 없습니다."),
    PERSONAL_ACCOUNT_NOT_FOUND("개인 계좌를 찾을 수 없습니다."),
    GATHERING_ACCOUNT_INVALID("유효하지 않은 모임 계좌입니다."),
    GATHERING_ALREADY_DELETED("이미 삭제된 모임입니다."),
    NOT_GATHERING_MEMBER("모임 회원이 아닙니다."),
    NOT_GATHERING_MANAGER("모임 총무가 아닙니다."),
    INVALID_GATHERING_STATUS("유효하지 않은 모임 상태입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),
    INVALID_USER_CREDENTIALS("잘못된 사용자 인증 정보입니다."),
    GATHERING_NAME_DUPLICATE("이미 존재하는 계좌명 입니다."),
    INVALID_PASSWORD("잘못된 비밀번호입니다."),
    DUPLICATE_EMAIL("이미 사용중인 이메일입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    INVALID_MANAGER("모임 관리자가 아닙니다."),
    SCHEDULE_BUDGET_NOT_SET("일정 예산이 설정되지 않았습니다."),
    ALREADY_SCHEDULE_MEMBER("이미 일정 멤버입니다."),
    SCHEDULE_MEMBER_NOT_FOUND("일정 멤버를 찾을 수 없습니다."),
    INVALID_MEMBER_STATUS("유효하지 않은 회원 상태입니다."),
    AUTO_PAYMENT_NOT_FOUND("자동이체를 찾을 수 없습니다."),
    INSUFFICIENT_BALANCE("잔액이 부족합니다."), GATHERING_MEMBER_NOT_FOUND("모임 멤버를 찾알 수 없습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}