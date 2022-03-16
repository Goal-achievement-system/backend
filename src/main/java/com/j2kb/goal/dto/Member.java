package com.j2kb.goal.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Member {
    private String email;
    private String password;
    private String nickName;
    private Sex sex;
    private Age age;

    public static enum Sex{
        MALE,
        FEMALE,
        UNKNOWN;
    }

    public static enum Age{
        AGE10,
        AGE20,
        AGE30,
        AGE40,
        AGE50,
        AGE60,
        AGESILVER;
    }
}
