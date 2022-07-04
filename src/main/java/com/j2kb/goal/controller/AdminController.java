package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Admin;
import com.j2kb.goal.dto.Announcement;
import com.j2kb.goal.dto.GoalAndCert;
import com.j2kb.goal.exception.NoMatchedAdminException;
import com.j2kb.goal.service.AbstractAdminService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        @AllArgsConstructor
        @Getter
        class GoalAndCertsAndPAge{
            int maxPage;
            List<GoalAndCert> results;
        }
        try {
            List<GoalAndCert> result = adminService.getHoldGoalAndCerts(page);
            int maxPage = Integer.parseInt(result.get(result.size()-1).getCertification().getImage());
            result.remove(result.size()-1);
            return ResponseEntity.ok(new GoalAndCertsAndPAge(maxPage,result));
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
        Announcement result = adminService.addAnnouncement(announcement);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/announcement")
    public ResponseEntity<?> updateAnnouncement(@RequestBody Announcement announcement){
        adminService.updateAnnouncement(announcement);
        return ResponseEntity.ok().build();
    }
}
