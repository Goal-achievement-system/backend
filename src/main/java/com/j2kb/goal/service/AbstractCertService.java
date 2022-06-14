package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.GoalState;

import java.util.List;
import java.util.Optional;

public interface AbstractCertService {
    void addCert(Certification certification, String goalOwnerEmail);
    Certification getCertificationByGoalId(long goalId);
    List<Certification> getCertificationsByEmail(String email, GoalState goalState, int page);
    int getCertificationsMaxPAgeByEmail(String email, GoalState goalState);
}
