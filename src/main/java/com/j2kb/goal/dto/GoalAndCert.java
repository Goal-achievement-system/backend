package com.j2kb.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoalAndCert {
    private Goal goal;
    private Certification certification;
}
