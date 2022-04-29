package com.j2kb.goal.service;

import com.j2kb.goal.dto.*;
import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.repository.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    @Override
    public void success(long goalId,String requestEmail) {
        Goal goal = goalRepository.selectGoalByGoalId(goalId, new GoalRowMapperIncludeEmail()).orElse(Goal.builder().build());
        if(canVerfication(goal,requestEmail)) {
            Certification certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
            Verification verification = new Verification(0,requestEmail,certification.getCertId());
            verificationRepository.insertVerification(verification);
            certificationRepository.increaseSuccessCount(goalId);
            certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
            if(isVerificationResultChange(goal,certification)){
                String certificationVerificationResult = certification.getVerificationResult();
                goalRepository.updateGoalVerificationResult(goalId,certificationVerificationResult);
            }
            if(isCertificationVerificationResultEqualSuccess(certification)){
                Member member = memberRepository.selectMemberByMemberEmail(goal.getMemberEmail());
                memberRepository.plusMoney(member,(int)(goal.getMoney()*1.5));
                notificationRepository.insertNotification(makeNotification(goal.getMemberEmail(),"목표달성에 성공했습니다. 상금이 지급되었습니다."));
            }
        }else{
            throw new PermissionException("self verification is not invalid");
        }
    }
    private Notification makeNotification(String email,String content){
        Notification.NotificationBuilder builder = Notification.builder();
        builder.memberEmail(email).content(content);
        return builder.build();
    }
    private boolean isVerificationResultChange(Goal before, Certification after){
        return !before.getVerificationResult().contentEquals(after.getVerificationResult());
    }
    private boolean isCertificationVerificationResultEqualSuccess(Certification certification){
        return certification.getVerificationResult().contentEquals("success");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void fail(long goalId,String requestEmail) {
        Goal goal = goalRepository.selectGoalByGoalId(goalId,new GoalRowMapperIncludeEmail()).orElse(Goal.builder().build());
        if(canVerfication(goal,requestEmail)) {
            Certification certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
            Verification verification = new Verification(0,requestEmail,certification.getCertId());
            verificationRepository.insertVerification(verification);
            certificationRepository.increaseFailCount(goalId);
            certification = certificationRepository.selectCertificationByGoalId(goalId).orElse(Certification.builder().build());
            if(isVerificationResultChange(goal,certification)){
                String certificationVerificationResult = certification.getVerificationResult();
                goalRepository.updateGoalVerificationResult(goalId,certificationVerificationResult);
            }
            if(isCertificationVerificationResultEqualHold(certification)){
                notificationRepository.insertNotification(makeNotification(goal.getMemberEmail(),"실패검증 횟수가 누적되어 인증이 보류되었습니다. 이의제기 하기 전 까지 판정이 보류됩니다."));
            }
        }else{
            throw new PermissionException("self verification is not invalid");
        }
    }
    private boolean isCertificationVerificationResultEqualHold(Certification certification){
        return certification.getVerificationResult().contentEquals("hold");
    }
    private boolean canVerfication(Goal goal, String requestEmail){
        return !goal.getMemberEmail().contentEquals(requestEmail);
    }
}
