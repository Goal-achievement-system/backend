package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;

import java.util.Optional;

public interface AbstractCertService {
    void addCert(Certification certification, String goalOwnerEmail);
    Certification getCertificationByGoalId(long goalId);
}
