package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Goal;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    void insertGoal(Goal goal);
    Optional<Goal> selectGoalByGoalId(long GoalId);
    
    List<Goal> selectAllGoalsByEmail(String email);
    List<Goal> selectUnAchievedGoalsByEmail(String email);
    List<Goal> selectAchievedGoalsByEmail(String email);

    long selectAllGoalsCountByEmail(String email);
    long selectUnAchievedGoalsCountByEmail(String email);
    long selectAchievedGoalsCountByEmail(String email);
}
