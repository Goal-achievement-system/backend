package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Admin;
import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalAndCert;

import java.util.List;

public interface AdminRepository {
    public boolean login(Admin admin);
    public List<GoalAndCert> selectHoldGoalAndCerts(int page);
    public void updateGoalVerificationResult(Goal goal);
    public void insertAnnouncement(Announcement announcement);
}