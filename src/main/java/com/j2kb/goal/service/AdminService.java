package com.j2kb.goal.service;

import com.j2kb.goal.dto.Admin;
import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalAndCert;
import com.j2kb.goal.exception.NoMatchedGoalException;
import com.j2kb.goal.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class AdminService implements AbstractAdminService{

    private AdminRepository adminRepository;


    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public String login(Admin admin) {
        return null;
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
            throw new NoMatchedGoalException("no matched goal with goalId = "+goalId);
        }
    }

    @Override
    public void failGoal(long goalId) {
        try{
            Goal goal = Goal.builder().goalId(goalId).verificationResult("fail").build();
            adminRepository.updateGoalVerificationResult(goal);
        }catch (DataAccessException e){
            throw new NoMatchedGoalException("no matched goal with goalId = "+goalId);
        }
    }

    @Override
    public Announcement addAnnouncement(Announcement announcement) {
        adminRepository.insertAnnouncement(announcement);
        return announcement;
    }
}
