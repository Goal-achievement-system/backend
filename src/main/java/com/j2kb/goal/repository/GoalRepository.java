package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Goal;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    void insertGoal(Goal goal);
    Optional<Goal> selectGoalByGoalId(long GoalId);
    
    List<Goal> selectAllGoalsByEmail(String email);
    List<Goal> selectFailGoalsByEmail(String email);
    List<Goal> selectSuccessGoalsByEmail(String email);
    List<Goal> selectOnGoingGoalsByEmail(String email);
    List<Goal> selectHoldGoalsByEmail(String email);
    
    List<Goal> selectAllGoalsByCategory(String category);
    List<Goal> selectFailGoalsByCategory(String category);
    List<Goal> selectSuccessGoalsByCategory(String category);
    List<Goal> selectOnGoingGoalsByCategory(String category);
    List<Goal> selectHoldGoalsByCategory(String category);
    
    long selectAllGoalsCount();
    long selectAllSuccessGoalsCount();
    long selectAllFailGoalsCount();
    long selectAllOngoingGoalsCount();
}
