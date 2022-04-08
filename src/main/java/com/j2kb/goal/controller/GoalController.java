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

    @PostMapping("")
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

    @GetMapping("/{category}/list/{state}/{page:[0-9]+}")
    public List<Goal> getGoalsByCategory(@PathVariable String category, @PathVariable String state, @PathVariable int page){
        List<Goal> result = goalService.getGoalsByCategoryAndState(category,state,page);
        return result;
    }

    @GetMapping("/cert/{goalId:[0-9]+}")
    public Certification getCertificationByGoalId(@PathVariable long goalId){
        Optional<Certification> result = certService.getCertificationByGoalId(goalId);
        return result.orElse(Certification.builder().build());
    }

    @PostMapping("/cert/{goalId:[0-9]+}")
    public Certification addCertificationByGoalId(@PathVariable long goalId, @RequestBody Certification certification, @RequestHeader("Authorization") String token){
        String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
        certService.addCert(certification,goalOwnerEmail);
        return certService.getCertificationByGoalId(goalId).orElse(Certification.builder().build());
    }

    @PutMapping("/cert/success/{goalId:[0-9]+}")
    public boolean successVerification(@PathVariable long goalId,@RequestHeader("Authorization") String token){
        String requestEmail = JwtBuilder.getEmailFromJwt(token);
        return verfiService.success(goalId,requestEmail);
    }

    @PutMapping("/cert/fail/{goalId:[0-9]+}")
    public boolean failVerification(@PathVariable long goalId,@RequestHeader("Authorization") String token){
        String requestEmail = JwtBuilder.getEmailFromJwt(token);
        return verfiService.fail(goalId,requestEmail);
    }
}
