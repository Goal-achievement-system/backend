package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;

import java.util.List;
import java.util.Optional;

public interface AbstractGoalService {
    Optional<Goal> getGoalByGoalId(long goalId);
    Goal addGoal(Goal goal,String email);
    List<Goal> getGoalsByCategoryAndState(String category, GoalState state,int page);
    List<Goal> getGoalsByEmailAndState(String email, GoalState state, int page);
    List<Goal> getOnCertificationGoalsByCategory(String category, int page);
    List<String> getCategories();
}
