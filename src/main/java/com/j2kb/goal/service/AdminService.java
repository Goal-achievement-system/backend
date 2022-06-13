package com.j2kb.goal.service;

import com.j2kb.goal.dto.*;
import com.j2kb.goal.exception.NoMatchedGoalException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.AdminRepository;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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


    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
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
        return adminRepository.selectHoldGoalAndCerts(page);
    }

    @Override
    public void successGoal(long goalId) {
        try{
            Goal goal = Goal.builder().goalId(goalId).verificationResult("success").build();
            adminRepository.updateGoalVerificationResult(goal);
        }catch (DataAccessException e){
            throw new NoMatchedGoalException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,"/api/admin/goals/cert/success/"+goalId,"no matched goal with goalId = "+goalId);
        }
    }

    @Override
    public void failGoal(long goalId) {
        try{
            Goal goal = Goal.builder().goalId(goalId).verificationResult("fail").build();
            adminRepository.updateGoalVerificationResult(goal);
        }catch (DataAccessException e){
            throw new NoMatchedGoalException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,"/api/admin/goals/cert/fail/"+goalId,"no matched goal with goalId = "+goalId);
        }
    }

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
            long announcementId = adminRepository.insertAnnouncement(announcement);
            announcement.setAnnouncementId(announcementId);
            announcement.setDate(Timestamp.valueOf(LocalDateTime.now()));
            fileName = "announcement"+File.separator+announcement.getAnnouncementId();
            imageFile.renameTo(new File(fileName));
            System.out.println(fileName);
            announcement.setImage("");
            return announcement;
        }catch (IllegalArgumentException e){
            throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.UNKNOWN,"POST /api/admin/announcement","DataURI 이미지가 아닙니다.");
        } catch (IOException e) {
            throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.UNKNOWN,"POST /api/admin/announcement","DataURI 를 파일로 바꿀 수 없습니다.");
        }
    }
}
