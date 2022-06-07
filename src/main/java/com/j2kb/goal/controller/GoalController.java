package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.exception.DuplicateCertificationException;
import com.j2kb.goal.exception.MoneyOverflowException;
import com.j2kb.goal.exception.NoMatchedCategoryException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.service.*;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
//ToDo Goal 조회기능에는 memberEmail이 포함되면 안됨.
@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private AbstractGoalService goalService;
    private AbstractCertService certService;
    private AbstractVerfiService verfiService;

    @Autowired
    public GoalController(GoalService goalService,CertService certService, VerfiService verfiService){
        this.goalService = goalService;
        this.certService = certService;
        this.verfiService = verfiService;
    }

    @PostMapping("")
    public ResponseEntity<Goal> addNewGoal(@RequestBody Goal goal,@RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        try {
            return ResponseEntity.ok(goalService.addGoal(goal, email));
        }catch (NoMatchedCategoryException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(){
        List<String> result = goalService.getCategories();
        if(result.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }else{
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/{goalId:[0-9]+}")
    public ResponseEntity<Goal> getGoalByGoalId(@PathVariable long goalId){
        try {
            Goal goal = goalService.getGoalByGoalId(goalId).orElseThrow();
            return ResponseEntity.ok(goal);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{category}/list/{state}/{page:[0-9]+}")
    public ResponseEntity<List<Goal>> getGoalsByCategoryAndState(@PathVariable String category, @PathVariable GoalState state, @PathVariable int page){
        try {
            List<Goal> result = goalService.getGoalsByCategoryAndState(category, state, page);
            return ResponseEntity.ok(result);
        }catch (NoMatchedCategoryException | IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{category}/list/{page:[0-9]+}")
    public ResponseEntity<List<Goal>> getOnCertificationGoalsByCategory(@PathVariable String category, @PathVariable int page){
        try {
            List<Goal> result = goalService.getOnCertificationGoalsByCategory(category,page);
            return ResponseEntity.ok(result);
        }catch (NoMatchedCategoryException | IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cert/{goalId:[0-9]+}")
    public ResponseEntity<Certification> getCertificationByGoalId(@PathVariable long goalId){
        try {
            Certification result = certService.getCertificationByGoalId(goalId).orElseThrow();
            return ResponseEntity.ok(result);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/cert/{goalId:[0-9]+}")
    public ResponseEntity<Certification> addCertificationByGoalId(@PathVariable long goalId, @RequestBody Certification certification, @RequestHeader("Authorization") String token){
        String goalOwnerEmail = JwtBuilder.getEmailFromJwt(token);
        try {
            certService.addCert(certification, goalOwnerEmail);
            return ResponseEntity.ok(certService.getCertificationByGoalId(goalId).orElseThrow());
        }catch (DataAccessException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @PutMapping("/cert/success/{goalId:[0-9]+}")
    public ResponseEntity<?> successVerification(@PathVariable long goalId,@RequestHeader("Authorization") String token){
        String requestEmail = JwtBuilder.getEmailFromJwt(token);
        try{
            verfiService.success(goalId,requestEmail);
            return ResponseEntity.ok().build();
        }catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/cert/fail/{goalId:[0-9]+}")
    public ResponseEntity<?> failVerification(@PathVariable long goalId, @RequestHeader("Authorization") String token){
        String requestEmail = JwtBuilder.getEmailFromJwt(token);
        try{
            verfiService.fail(goalId,requestEmail);
            return ResponseEntity.ok().build();
        }catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
