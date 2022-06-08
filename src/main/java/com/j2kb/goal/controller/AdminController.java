package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Admin;
import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.GoalAndCert;
import com.j2kb.goal.exception.NoMatchedAdminException;
import com.j2kb.goal.service.AbstractAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AbstractAdminService adminService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin admin){
        try {
            String token = adminService.login(admin);
            Map<String,String> result = new HashMap<>();
            result.put("Authorization",token);
            return ResponseEntity.ok(result);
        }catch (NoMatchedAdminException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/goals/hold/{page:[0-9]+}")
    public ResponseEntity<?> getHoldGoalAndCerts(@PathVariable int page){
        try {
            List<GoalAndCert> result = adminService.getHoldGoalAndCerts(page);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @PutMapping("/goals/cert/success/{goalId:[0-9]+}")
    public ResponseEntity<?> successGoal(@PathVariable long goalId){
        adminService.successGoal(goalId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/goals/cert/fail/{goalId:[0-9]+}")
    public ResponseEntity<?> failGoal(@PathVariable long goalId){
        adminService.failGoal(goalId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/announcement")
    public ResponseEntity<?> addNewAnnouncement(@RequestBody Announcement announcement){
        try{
            Announcement result = adminService.addAnnouncement(announcement);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
