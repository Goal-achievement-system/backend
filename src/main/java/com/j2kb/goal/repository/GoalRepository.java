package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Goal;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {

    List<String> selectAllCategories();

    Goal insertGoal(Goal goal);
    Optional<Goal> selectGoalByGoalId(long goalId);
    Optional<Goal> selectGoalByGoalId(long goalId, RowMapper<Goal> rowMapper);

    void updateGoalVerificationResult(long goalId, String result);

    List<Goal> selectAllGoalsByEmail(String email,int page);
    List<Goal> selectFailGoalsByEmail(String email,int page);
    List<Goal> selectSuccessGoalsByEmail(String email,int page);
    List<Goal> selectOnGoingGoalsByEmail(String email,int page);
    List<Goal> selectHoldGoalsByEmail(String email,int page);
    
    List<Goal> selectAllGoalsByCategory(String category,int page);
    List<Goal> selectFailGoalsByCategory(String category,int page);
    List<Goal> selectSuccessGoalsByCategory(String category,int page);
    List<Goal> selectOnGoingGoalsByCategory(String category,int page);
    List<Goal> selectHoldGoalsByCategory(String category,int page);
    
    long selectAllGoalsCount();
    long selectAllSuccessGoalsCount();
    long selectAllFailGoalsCount();
    long selectAllOngoingGoalsCount();
    
    long selectMemberGoalsCount(String email);
    long selectMemberSuccessGoalsCount(String email);
    long selectMemberFailGoalsCount(String email);
    long selectMemberOngoingGoalsCount(String email);
    long selectMemberHoldGoalsCount(String email);
}
