package com.j2kb.goal.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Member {
    private String email;
    private String password;
    private String nickName;
    private Sex sex;
    private byte age;
    private int money;

    public static enum Sex{
        MALE,
        FEMALE,
        UNKNOWN;
    }
}
