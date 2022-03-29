package com.j2kb.goal.controller;

import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.JdbcTemplateGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
