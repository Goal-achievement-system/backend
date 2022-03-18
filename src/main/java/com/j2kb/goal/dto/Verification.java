package com.j2kb.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Verification {
    private long verificationId;
    private String memberEmail;
    private long certId;
}
