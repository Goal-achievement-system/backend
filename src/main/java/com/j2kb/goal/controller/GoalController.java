package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.service.CertService;
import com.j2kb.goal.service.GoalService;
import com.j2kb.goal.service.VerfiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private GoalService goalService;
    private CertService certService;
    private VerfiService verfiService;

    @Autowired
    public GoalController(GoalService goalService,CertService certService, VerfiService verfiService){
        this.goalService = goalService;
        this.certService = certService;
        this.verfiService = verfiService;
    }

    @PostMapping("/")
    public Goal addNewGoal(@RequestBody Goal goal){
        return goalService.addGoal(goal);
    }

    @GetMapping("/categories")
    public List<String> getCategories(){
        return goalService.getCategories();
    }

    @GetMapping("/{goalId:[0-9]+}")
    public Goal getGoalByGoalId(@PathVariable long goalId){
        return goalService.getGoalByGoalId(goalId).orElse(Goal.builder().build());
    }

    @GetMapping("/{category}/list/{state}")
    public List<Goal> getGoalsByCategory(@PathVariable String category, @PathVariable String state){
        return goalService.getGoalsByCategoryAndState(category,state);
    }

    @GetMapping("/cert/{goalId:[0-9]+}")
    public Certification getCertificationByGoalId(@PathVariable long goalId){
        Optional<Certification> result = certService.getCertificationByGoalId(goalId);
        return result.orElse(Certification.builder().build());
    }

    @PutMapping("/goals/cert/success/{goalId:[0-9]+}")
    public void successVerification(long goalId){
        verfiService.success(goalId);
    }

    @PutMapping("/goals/cert/fail/{goalId:[0-9]+}")
    public void failVerification(long goalId){
        verfiService.fail(goalId);
    }
}
