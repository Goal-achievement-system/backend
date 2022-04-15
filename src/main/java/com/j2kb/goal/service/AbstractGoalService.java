package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;

import java.util.List;
import java.util.Optional;

public interface AbstractGoalService {
    Optional<Goal> getGoalByGoalId(long goalId);
    Goal addGoal(Goal goal,String email);
    List<Goal> getGoalsByCategoryAndState(String category, String state,int page);
    List<Goal> getGoalsByEmailAndState(String email, String state, int page);
    List<String> getCategories();
}
