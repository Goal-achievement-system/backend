package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;

import java.util.Optional;

public interface AbstractCertService {
    boolean addCert(Certification certification, String goalOwnerEmail);
    Optional<Certification> getCertificationByGoalId(long goalId);
}
