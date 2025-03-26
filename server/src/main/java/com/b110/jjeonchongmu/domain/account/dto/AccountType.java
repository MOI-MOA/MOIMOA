package com.b110.jjeonchongmu.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    GATHERING, PERSONAL, SCHEDULE;

    @JsonCreator
    public static AccountType fromString(String value) {
        return value == null ? null : AccountType.valueOf(value);
    }

    @JsonValue
    public String toString() {
        return name();
    }
}