package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.JdbcTemplateGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private GoalRepository repository;

    @Autowired
    public GoalController(GoalRepository repository){
        this.repository = repository;
    }

    @GetMapping("/categories")
    public List<String> getCategories(){
        return repository.selectAllCategories();
    }

    @GetMapping("/{goalId:[0-9]+}")
    public Goal getGoalByGoalId(@PathVariable long goalId){
        return repository.selectGoalByGoalId(goalId).orElse(Goal.builder().build());
    }

    @GetMapping("/{category}/list")
    public List<Goal> getGoalsByCategory(@PathVariable String category){
        return repository.selectAllGoalsByCategory(category);
    }

}
