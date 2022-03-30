package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.repository.CertificationRepository;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.JdbcTemplateGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private GoalRepository goalRepository;
    private CertificationRepository certificationRepository;

    @Autowired
    public GoalController(GoalRepository goalRepository,CertificationRepository certificationRepository){
        this.goalRepository = goalRepository;
        this.certificationRepository = certificationRepository;
    }

    @PostMapping("/")
    public Goal addNewGoal(@RequestBody Goal goal){
        return goalRepository.insertGoal(goal);
    }

    @GetMapping("/categories")
    public List<String> getCategories(){
        return goalRepository.selectAllCategories();
    }

    @GetMapping("/{goalId:[0-9]+}")
    public Goal getGoalByGoalId(@PathVariable long goalId){
        return goalRepository.selectGoalByGoalId(goalId).orElse(Goal.builder().build());
    }

    @GetMapping("/{category}/list")
    public List<Goal> getGoalsByCategory(@PathVariable String category){
        return goalRepository.selectAllGoalsByCategory(category);
    }

    @GetMapping("/cert/{goalId:[0-9]+}")
    public Certification getCertificationByGoalId(@PathVariable long goalId){
        Optional<Certification> result = certificationRepository.selectCertificationByGoalId(goalId);
        return result.orElse(Certification.builder().build());
    }

    @PutMapping("/goals/cert/success/{goalId:[0-9]+}")
    public void successVerification(long goalId){
        certificationRepository.increaseSuccessCount(goalId);
    }

    @PutMapping("/goals/cert/fail/{goalId:[0-9]+}")
    public void failVerification(long goalId){
        certificationRepository.increaseSuccessCount(goalId);
    }
}
