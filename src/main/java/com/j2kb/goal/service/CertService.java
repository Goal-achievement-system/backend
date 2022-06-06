package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.exception.DuplicateCertificationException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.repository.CertificationRepository;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.GoalRowMapperIncludeEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class CertService implements AbstractCertService{
    private CertificationRepository certificationRepository;
    private GoalRepository goalRepository;

    @Autowired
    public CertService(CertificationRepository certificationRepository, GoalRepository goalRepository){
        this.certificationRepository = certificationRepository;
        this.goalRepository = goalRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addCert(Certification certification, String goalOwnerEmail) {
        Goal goal = goalRepository.selectGoalByGoalId(certification.getGoalId(),new GoalRowMapperIncludeEmail()).orElseThrow(()->new RuntimeException("No matched goal"));
        if(!goal.getMemberEmail().contentEquals(goalOwnerEmail)){
            throw new PermissionException("goalOwner = "+goal.getMemberEmail() +", requester = "+goalOwnerEmail);
        }
        certificationRepository.insertCertification(certification);
        goalRepository.updateGoalVerificationResult(goal.getGoalId(), GoalState.oncertification.name());
    }

    @Override
    public Optional<Certification> getCertificationByGoalId(long goalId) {
        return certificationRepository.selectCertificationByGoalId(goalId);
    }
}
