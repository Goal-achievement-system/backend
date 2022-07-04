package com.j2kb.goal.service;

import com.j2kb.goal.dto.*;
import com.j2kb.goal.exception.NoMatchedGoalException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.*;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class AdminService implements AbstractAdminService{

    private AdminRepository adminRepository;
    private MemberRepository memberRepository;
    private GoalRepository goalRepository;
    private NotificationRepository notificationRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository, MemberRepository memberRepository,GoalRepository goalRepository, NotificationRepository notificationRepository) {
        this.adminRepository = adminRepository;
        this.memberRepository = memberRepository;
        this.goalRepository = goalRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public String login(Admin admin) {
        try {
            if (adminRepository.login(admin)) {
                return JwtBuilder.build(admin.getEmail());
            } else {
                throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.LOGIN_FAIL,"POST /api/admin/login","email or password is wrong");
            }
        }catch (DataAccessException e){
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.LOGIN_FAIL,"POST /api/admin/login","email or password is wrong");
        }
    }

    @Override
    public List<GoalAndCert> getHoldGoalAndCerts(int page) {
        List<GoalAndCert> result = adminRepository.selectHoldGoalAndCerts(page);
        int maxPage = adminRepository.selectHoldGoalAndCertsMaxPage();
        result.add(new GoalAndCert(null,Certification.builder().image(maxPage+"").build()));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void successGoal(long goalId) {
        try{
            Goal goal = goalRepository.selectGoalByGoalId(goalId,new GoalRowMapperIncludeEmail()).get();
            goal.setVerificationResult("success");
            adminRepository.updateGoalVerificationResult(goal);
            String reward = goal.getReward();
            int money = 0;
            if(reward.contentEquals("high")){
                money = (int)((double)(goal.getMoney())*1.5);
            }else {
                money = (int)((double)(goal.getMoney())*1.1);
            }
            memberRepository.plusMoney(Member.builder().email(goal.getMemberEmail()).build(),money);
            Notification.NotificationBuilder builder = Notification.builder();
            builder.url("GET /api/members/myinfo")
                    .content("카테고리 : 목표검증;보류된 목표의 검토결과 최종 성공처리 되었습니다.")
                    .memberEmail(goal.getMemberEmail());
            notificationRepository.insertNotification(builder.build());
        }catch (DataAccessException e){
            throw new NoMatchedGoalException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,"/api/admin/goals/cert/success/"+goalId,"no matched goal with goalId = "+goalId);
        }
    }

    @Override
    public void failGoal(long goalId) {
        try{
            Goal goal = goalRepository.selectGoalByGoalId(goalId,new GoalRowMapperIncludeEmail()).get();
            goal.setVerificationResult("fail");
            adminRepository.updateGoalVerificationResult(goal);
            String reward = goal.getReward();
            int money = 0;
            if(reward.contentEquals("high")){
            }else {
                money = (int)((double)(goal.getMoney())*0.5);
            }
            memberRepository.plusMoney(Member.builder().email(goal.getMemberEmail()).build(),money);
            Notification.NotificationBuilder builder = Notification.builder();
            builder.url("GET /api/members/myinfo")
                    .content("카테고리 : 목표검증;보류된 목표의 검토결과 최종 실패처리 되었습니다.")
                    .memberEmail(goal.getMemberEmail());
            notificationRepository.insertNotification(builder.build());
        }catch (DataAccessException e){
            throw new NoMatchedGoalException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,"/api/admin/goals/cert/fail/"+goalId,"no matched goal with goalId = "+goalId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Announcement addAnnouncement(Announcement announcement) {
        String image = announcement.getImage();
        try {
            byte[] imageData = java.util.Base64.getDecoder().decode(image.substring(image.indexOf(",") + 1));
            String filenameExtension  = image.split(",")[0].split("/")[1].split(";")[0];
            String fileName = "announcement"+File.separator+announcement.getAnnouncementId();
            File imageFile = new File(fileName);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            ImageIO.write(bufferedImage,filenameExtension,imageFile);
            announcement.setImage(fileName);
            String banner = announcement.getBannerImage();
            announcement.setBannerImage("announcement"+File.separator+"banner"+announcement.getAnnouncementId());
            long announcementId = adminRepository.insertAnnouncement(announcement);
            announcement.setBannerImage(banner);
            announcement.setAnnouncementId(announcementId);
            announcement.setDate(Timestamp.valueOf(LocalDateTime.now()));
            fileName = "announcement"+File.separator+announcement.getAnnouncementId();
            imageFile.renameTo(new File(fileName));
            System.out.println(fileName);
            announcement.setImage("");
            saveBannerImage(announcement);
            return announcement;
        }catch (IllegalArgumentException e){
            throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.UNKNOWN,"POST /api/admin/announcement","DataURI 이미지가 아닙니다.");
        } catch (IOException e) {
            throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.UNKNOWN,"POST /api/admin/announcement","DataURI 를 파일로 바꿀 수 없습니다.");
        }
    }
    private void saveBannerImage(Announcement announcement) throws IOException{
        String bannerImage = announcement.getBannerImage();
        byte[] imageData = java.util.Base64.getDecoder().decode(bannerImage.substring(bannerImage.indexOf(",") + 1));
        String filenameExtension  = bannerImage.split(",")[0].split("/")[1].split(";")[0];
        String fileName = "announcement"+File.separator+"banner"+announcement.getAnnouncementId();
        File imageFile = new File(fileName);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
        ImageIO.write(bufferedImage,filenameExtension,imageFile);
    }
    @Override
    public void updateAnnouncement(Announcement announcement) {
        try{
            adminRepository.updateAnnouncement(announcement);
        }catch (DataAccessException e){
            throw new SpringHandledException(HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND,"PUT /api/admin/announcement","해당 공지사항이 없습니다");
        }
    }
}
