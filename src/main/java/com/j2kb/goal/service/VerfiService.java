package com.j2kb.goal.service;

import com.j2kb.goal.dto.*;
import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.repository.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class VerfiService implements AbstractVerfiService{
    private CertificationRepository certificationRepository;
    private GoalRepository goalRepository;
    private VerificationRepository verificationRepository;
    private MemberRepository memberRepository;
    private NotificationRepository notificationRepository;
    public VerfiService(CertificationRepository certificationRepository,GoalRepository goalRepository,VerificationRepository verificationRepository,MemberRepository memberRepository,NotificationRepository notificationRepository){
        this.certificationRepository = certificationRepository;
        this.goalRepository = goalRepository;
        this.verificationRepository = verificationRepository;
        this.memberRepository = memberRepository;
        this.notificationRepository = notificationRepository;
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
                if(certification.getVerificationResult().contentEquals("success")){
                    Member member = memberRepository.selectMemberByMemberEmail(requestEmail);
                    memberRepository.plusMoney(member,(int)(goal.getMoney()*1.5));
                    Notification.NotificationBuilder builder = Notification.builder();
                    builder.memberEmail(goal.getMemberEmail())
                            .content("목표달성에 성공했습니다. 상금이 지급되었습니다.");
                    notificationRepository.insertNotification(builder.build());
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
                if(certification.getVerificationResult().contentEquals("hold")){
                    Notification.NotificationBuilder builder = Notification.builder();
                    builder.memberEmail(goal.getMemberEmail())
                            .content("실패검증 횟수가 누적되어 인증이 보류되었습니다. 이의제기 하기 전 까지 판정이 보류됩니다.");
                    notificationRepository.insertNotification(builder.build());
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
