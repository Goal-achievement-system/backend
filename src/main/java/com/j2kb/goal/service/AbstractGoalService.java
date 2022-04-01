package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;

import java.util.List;
import java.util.Optional;

public interface AbstractGoalService {
    Optional<Goal> getGoalByGoalId(long goalId);
    Goal addGoal(Goal goal);
    List<Goal> getGoalsByCategoryAndState(String category, String state);
    List<Goal> getGoalsByEmailAndState(String email, String state);
    List<String> getCategories();
}
