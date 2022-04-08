package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.dto.Notification;
import com.j2kb.goal.service.GoalService;
import com.j2kb.goal.service.MemberService;
import com.j2kb.goal.service.NotificationService;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private MemberService memberService;
    private NotificationService notificationService;
    private GoalService goalService;

    @Autowired
    public MemberController(MemberService memberService, NotificationService notificationService, GoalService goalService) {
        this.memberService = memberService;
        this.notificationService = notificationService;
        this.goalService = goalService;
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Member member){
        String token = memberService.login(member);
        Map<String,String> result = new HashMap<>();
        result.put("Authorization",token);
        return result;
    }
    @PostMapping("")
    public void join(@RequestBody Member member){
        memberService.join(member);
    }
    @GetMapping("/myinfo")
    public Member getMyInfo( @RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        return memberService.getMemberByEmail(email);
    }
    @GetMapping("/myinfo/goals/{state}")
    public List<Goal> getMyGoals(@RequestHeader("Authorization") String token, @PathVariable String state){
        String email = JwtBuilder.getEmailFromJwt(token);
        return goalService.getGoalsByEmailAndState(email,state);
    }
    @GetMapping("/myinfo/notifications")
    public List<Notification> getMyGoals(@RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        return notificationService.getNotificationsByEmail(email);
    }
    @PutMapping("/myinfo")
    public Member updateMyInfo(@RequestBody Member member, @RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        memberService.updateMember(member,email);
        return memberService.getMemberByEmail(email);
    }
    @PostMapping("/verfi")
    public boolean f(@RequestBody Map<String,String> map){
        return JwtBuilder.isValid(map.get("Authorization"));
    }
}
