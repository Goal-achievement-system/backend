package com.j2kb.goal.service;

public interface AbstractStatisticsService {
    long getAllGoalsCount();
    long getAllSuccessGoalsCount();
    long getAllFailGoalsCount();
    long getAllOngoingGoalsCount();
    long getMemberGoalsCount(String email);
    long getMemberSuccessGoalsCount(String email);
    long getMemberFailGoalsCount(String email);
    long getMemberOngoingGoalsCount(String email);
    long getMemberHoldGoalsCount(String email);
}
