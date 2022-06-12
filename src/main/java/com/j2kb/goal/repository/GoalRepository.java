package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    public static final int MYINFO_GOAL_COUNT=6;
    public static final int GOAL_COUNT=6;

    List<String> selectAllCategories();

    Goal insertGoal(Goal goal);
    Optional<Goal> selectGoalByGoalId(long goalId);
    Optional<Goal> selectGoalByGoalId(long goalId, RowMapper<Goal> rowMapper);

    void updateGoalVerificationResult(long goalId, String result);

    List<Goal> selectGoalsByEmailAndState(String email, GoalState state, int page);
    long selectCountOfGoalsByEmailAndState(String email, GoalState state);
    List<Goal> selectGoalsByCategoryAndState(String category, GoalState state, int page);
    long selectCountOfGoalsByCategoryAndState(String category, GoalState state);

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
