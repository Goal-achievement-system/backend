package com.j2kb.goal.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class Goal {
    private long goalId;
    private String memberEmail;
    private String goalName;
    private String content;
    private Timestamp limitDate;
    private int money;
    private String reward;
    private String verificationResult;
}
