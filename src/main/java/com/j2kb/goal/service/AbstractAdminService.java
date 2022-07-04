package com.j2kb.goal.service;

import com.j2kb.goal.dto.Admin;
import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalAndCert;

import java.util.List;

public interface AbstractAdminService {
    String login(Admin admin);
    List<GoalAndCert> getHoldGoalAndCerts(int page);
    void successGoal(long certId);
    void failGoal(long certId);
    Announcement addAnnouncement(Announcement announcement);
    void updateAnnouncement(Announcement announcement);
}
