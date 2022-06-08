package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.exception.DuplicateCertificationException;
import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.repository.CertificationRepository;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.GoalRowMapperIncludeEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
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
            throw new PermissionException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED, "/api/goals/cert/"+certification.getGoalId(), "You cannot register for certification of goals registered by other members.");
        }
        certificationRepository.insertCertification(certification);
        goalRepository.updateGoalVerificationResult(goal.getGoalId(), GoalState.oncertification.name());
    }

    @Override
    public Certification getCertificationByGoalId(long goalId) {
        try {
            return certificationRepository.selectCertificationByGoalId(goalId).orElseThrow();
        }catch (DataAccessException | NoSuchElementException e){
            throw new NoMatchedCertificationException(HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND,"GET /api/goals/cert/"+goalId,"No matched Certification");
        }
    }
}
