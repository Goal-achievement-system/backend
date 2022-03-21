package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Certification;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository {
    void insertCertification(Certification certification);

    List<Certification> selectUnVerifiedCertifications();
    Optional<Certification> selectCertificationByGoalId(long goalId);

    void deleteCertification(Certification certification);

    void increaseSuccessCount(long certId);

    void increaseFailCount(long certId);

}
