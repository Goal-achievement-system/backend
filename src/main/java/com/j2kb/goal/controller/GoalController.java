package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.service.CertService;
import com.j2kb.goal.service.GoalService;
import com.j2kb.goal.service.VerfiService;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
//ToDo Goal 조회기능에는 memberEmail이 포함되면 안됨.
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
        Goal goal = goalService.getGoalByGoalId(goalId).orElse(Goal.builder().build());
        return goal;
    }

    @GetMapping("/{category}/list/{state}")
    public List<Goal> getGoalsByCategory(@PathVariable String category, @PathVariable String state){
        List<Goal> result = goalService.getGoalsByCategoryAndState(category,state);
        return result;
    }

    @GetMapping("/cert/{goalId:[0-9]+}")
    public Certification getCertificationByGoalId(@PathVariable long goalId){ // ToDo 이메일은 제외해야 함.
        Optional<Certification> result = certService.getCertificationByGoalId(goalId);
        return result.orElse(Certification.builder().build());
    }

    @PostMapping("/cert/{goalId:[0-9]+}")
    public void addCertificationByGoalId(@PathVariable long goalId, @RequestBody Certification certification, @RequestHeader("Authorization") String token){
        String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
        certService.addCert(certification,goalOwnerEmail);
    }

    @PutMapping("/goals/cert/success/{goalId:[0-9]+}")
    public void successVerification(long goalId,@RequestHeader("Authorization") String token){
        String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
        verfiService.success(goalId,goalOwnerEmail);
    }

    @PutMapping("/goals/cert/fail/{goalId:[0-9]+}")
    public void failVerification(long goalId,@RequestHeader("Authorization") String token){
        String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
        verfiService.fail(goalId,goalOwnerEmail);
    }
}
