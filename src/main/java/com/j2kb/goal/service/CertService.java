package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.exception.*;
import com.j2kb.goal.repository.CertificationRepository;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.GoalRowMapperIncludeEmail;
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
import java.util.Base64;
import java.util.List;
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
        String imgDataURI = certification.getImage();
        String fileName = saveFile(imgDataURI,certification.getGoalId());
        certification.setImage(fileName);
        if(!goal.getMemberEmail().contentEquals(goalOwnerEmail)){
            throw new PermissionException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED, "POST /api/goals/cert/"+certification.getGoalId(), "You cannot register for certification of goals registered by other members.");
        }
        certificationRepository.insertCertification(certification);
        goalRepository.updateGoalVerificationResult(goal.getGoalId(), GoalState.oncertification.name());
    }
    private String saveFile(String imgDataURI, long goalId){
        try {
            if(imgDataURI.contains("data:image/")){
                byte[] imageData = Base64.getDecoder().decode(imgDataURI.substring(imgDataURI.indexOf(",") + 1));
                String filenameExtension  = imgDataURI.split(",")[0].split("/")[1].split(";")[0];
                String fileName = "cert"+ File.separator+goalId;
                File imageFile = new File(fileName);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
                ImageIO.write(bufferedImage,filenameExtension,imageFile);
                fileName = "cert"+File.separator+goalId;
                imageFile.renameTo(new File(fileName));
                System.out.println(fileName);
                return fileName;
            }else{
                throw new SpringHandledException(HttpStatus.BAD_REQUEST,ErrorCode.NOT_FOUND,"POST /api/goals/cert/"+goalId,"image not found");
            }
        }catch (Exception e){
            throw new SpringHandledException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorCode.UNKNOWN,"POST /api/goals/cert/"+goalId,"image save fail");
        }
    }
    @Override
    public Certification getCertificationByGoalId(long goalId) {
        try {
            return certificationRepository.selectCertificationByGoalId(goalId).orElseThrow();
        }catch (DataAccessException | NoSuchElementException e){
            throw new NoMatchedCertificationException(HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND,"GET /api/goals/cert/"+goalId,"No matched Certification");
        }
    }

    @Override
    public List<Certification> getCertificationsByEmail(String email, GoalState goalState, int page) {
        try {
            return certificationRepository.selectMembersCertificationByEmail(email, goalState, page);
        }catch (DataAccessException e){
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.INVALID_TOKEN,"GET api/members/myinfo/cert/"+goalState.name()+"/"+page,"wrong email");
        }
    }

    @Override
    public int getCertificationsMaxPAgeByEmail(String email, GoalState goalState) {
        int certCount = certificationRepository.selectMembersCertificationsMaxPageByEmail(email, goalState);
        if(certCount<=6) return 1;
        certCount = certCount / 6;
        if(certCount%6!=0)certCount++;
        return certCount;
    }
}
