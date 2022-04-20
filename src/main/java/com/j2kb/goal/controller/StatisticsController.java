package com.j2kb.goal.controller;

import com.j2kb.goal.service.AbstractStatisticsService;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private AbstractStatisticsService statisticsService;

    @GetMapping("/total")
    public ResponseEntity<?> getTotalStatistics(){
        long totalGoalCount = statisticsService.getAllGoalsCount();
        long totalSuccessGoalCount = statisticsService.getAllSuccessGoalsCount();
        long totalFailGoalCount = statisticsService.getAllFailGoalsCount();
        long totalOngoingGoalCount = statisticsService.getAllOngoingGoalsCount();
        Map<String,Long> result = new HashMap<>();
        result.put("totalGoalCount",totalGoalCount);
        result.put("totalSuccessGoalCount",totalSuccessGoalCount);
        result.put("totalFailGoalCount",totalFailGoalCount);
        result.put("totalOngoingGoalCount",totalOngoingGoalCount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMemberStatistics(@RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        long totalGoalCount = statisticsService.getMemberGoalsCount(email);
        long totalSuccessGoalCount = statisticsService.getMemberSuccessGoalsCount(email);
        long totalFailGoalCount = statisticsService.getMemberFailGoalsCount(email);
        long totalOngoingGoalCount = statisticsService.getMemberOngoingGoalsCount(email);
        long totalHoldGoalCount = statisticsService.getMemberHoldGoalsCount(email);
        Map<String,Long> result = new HashMap<>();
        result.put("totalGoalCount",totalGoalCount);
        result.put("totalSuccessGoalCount",totalSuccessGoalCount);
        result.put("totalFailGoalCount",totalFailGoalCount);
        result.put("totalOngoingGoalCount",totalOngoingGoalCount);
        result.put("totalHoldGoalCount",totalHoldGoalCount);
        return ResponseEntity.ok(result);
    }
}
