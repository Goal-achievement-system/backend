package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.Verification;
import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.repository.CertificationRepository;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.GoalRowMapperIncludeEmail;
import com.j2kb.goal.repository.VerificationRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class VerfiService implements AbstractVerfiService{
    private CertificationRepository certificationRepository;
    private GoalRepository goalRepository;
    private VerificationRepository verificationRepository;
    public VerfiService(CertificationRepository certificationRepository,GoalRepository goalRepository,VerificationRepository verificationRepository){
        this.certificationRepository = certificationRepository;
        this.goalRepository = goalRepository;
        this.verificationRepository = verificationRepository;
    }
    @Override
    public boolean success(long goalId,String requestEmail) {
        Goal goal = goalRepository.selectGoalByGoalId(goalId, new GoalRowMapperIncludeEmail()).orElse(Goal.builder().build());
        if(canVerfication(goal,requestEmail)) {
            try {
                Certification certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
                Verification verification = new Verification(0,requestEmail,certification.getCertId());
                verificationRepository.insertVerification(verification);
                certificationRepository.increaseSuccessCount(goalId);
                certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
                String certificationVerificationResult = certification.getVerificationResult();
                if(!goal.getVerificationResult().contentEquals(certificationVerificationResult)){
                    goalRepository.updateGoalVerificationResult(goalId,certificationVerificationResult);
                }
            } catch (DataAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean fail(long goalId,String requestEmail) {
        Goal goal = goalRepository.selectGoalByGoalId(goalId,new GoalRowMapperIncludeEmail()).orElse(Goal.builder().build());
        if(canVerfication(goal,requestEmail)) {
            try {
                Certification certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
                Verification verification = new Verification(0,requestEmail,certification.getCertId());
                verificationRepository.insertVerification(verification);
                certificationRepository.increaseFailCount(goalId);
                certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
                String certificationVerificationResult = certification.getVerificationResult();
                if(!goal.getVerificationResult().contentEquals(certificationVerificationResult)){
                    goalRepository.updateGoalVerificationResult(goalId,certificationVerificationResult);
                }
            } catch (DataAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean canVerfication(Goal goal, String requestEmail){
        System.out.println(!goal.getMemberEmail().contentEquals(requestEmail));
        return !goal.getMemberEmail().contentEquals(requestEmail);
    }
}
