package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.dto.GoalsWithPagination;

import java.util.List;
import java.util.Optional;

public interface AbstractGoalService {
    Goal getGoalByGoalId(long goalId);
    Goal addGoal(Goal goal,String email);
    GoalsWithPagination getGoalsByCategoryAndState(String category, GoalState state, int page);
    GoalsWithPagination getGoalsByEmailAndState(String email, GoalState state, int page);
    GoalsWithPagination getOnCertificationGoalsByCategory(String category, int page);
    List<String> getCategories();
}
