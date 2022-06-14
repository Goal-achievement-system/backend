package com.j2kb.goal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Certification {
    private long certId;
    private long goalId;
    private String content;
    @Setter
    private String image;
    private byte requireSuccessCount;
    private byte successCount;
    private byte failCount;
    private String verificationResult;
}
