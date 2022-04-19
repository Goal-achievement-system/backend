package com.j2kb.goal.service;

import com.j2kb.goal.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService implements AbstractStatisticsService{
    @Autowired
    private GoalRepository goalRepository;

    @Override
    public long getAllGoalsCount() {
        return goalRepository.selectAllGoalsCount();
    }

    @Override
    public long getAllSuccessGoalsCount() {
        return goalRepository.selectAllSuccessGoalsCount();
    }

    @Override
    public long getAllFailGoalsCount() {
        return goalRepository.selectAllFailGoalsCount();
    }

    @Override
    public long getAllOngoingGoalsCount() {
        return goalRepository.selectAllOngoingGoalsCount();
    }

    @Override
    public long getMemberGoalsCount(String email) {
        return goalRepository.selectMemberGoalsCount(email);
    }

    @Override
    public long getMemberSuccessGoalsCount(String email) {
        return goalRepository.selectMemberSuccessGoalsCount(email);
    }

    @Override
    public long getMemberFailGoalsCount(String email) {
        return goalRepository.selectMemberFailGoalsCount(email);
    }

    @Override
    public long getMemberOngoingGoalsCount(String email) {
        return goalRepository.selectMemberOngoingGoalsCount(email);
    }

    @Override
    public long getMemberHoldGoalsCount(String email) {
        return goalRepository.selectMemberHoldGoalsCount(email);
    }
}
