package com.j2kb.goal.service;

import com.j2kb.goal.dto.*;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.repository.*;
import org.springframework.http.HttpStatus;
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
                String reward = goal.getReward();
                int money;
                if(reward.contentEquals("high")){
                    money = (int)((double)(goal.getMoney())*1.5);
                }else{
                    money = (int)((double)(goal.getMoney())*1.1);
                }
                memberRepository.plusMoney(member,money);
                String url = "GET /api/members/myinfo";
                notificationRepository.insertNotification(makeNotification(goal.getMemberEmail(),"???????????? : ????????????;??????????????? ??????????????????. ????????? ?????????????????????.",url));
            }
        }else{
            throw new PermissionException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED, "/api/goals/cert/success/"+goalId, "self verification is not invalid");
        }
    }
    private Notification makeNotification(String email,String content,String url){
        Notification.NotificationBuilder builder = Notification.builder();
        builder.memberEmail(email).content(content).url(url);
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
                String url = "GET /api/members/myinfo";
                notificationRepository.insertNotification(makeNotification(goal.getMemberEmail(),"???????????? : ????????????;???????????? ????????? ???????????? ????????? ?????????????????????. ???????????? ?????? ??? ?????? ????????? ???????????????.",url));
            }
        }else{
            throw new PermissionException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED, "/api/goals/cert/fail/"+goalId, "self verification is not invalid");
        }
    }
    private boolean isCertificationVerificationResultEqualHold(Certification certification){
        return certification.getVerificationResult().contentEquals("hold");
    }
    private boolean canVerfication(Goal goal, String requestEmail){
        return !goal.getMemberEmail().contentEquals(requestEmail);
    }
}
