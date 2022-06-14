package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.GoalState;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository {
    void insertCertification(Certification certification);

    List<Certification> selectUnVerifiedCertifications();
    List<Certification> selectMembersCertificationByEmail(String email, GoalState goalState, int page);
    Optional<Certification> selectCertificationByGoalId(long goalId);

    int selectMembersCertificationsMaxPageByEmail(String email, GoalState goalState);

    void deleteCertification(Certification certification);

    void increaseSuccessCount(long goalId);

    void increaseFailCount(long goalId);

}
