package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.dto.Notification;
import com.j2kb.goal.exception.DuplicateMemberException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.service.GoalService;
import com.j2kb.goal.service.MemberService;
import com.j2kb.goal.service.NotificationService;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> login(@RequestBody Member member){
        try {
            String token = memberService.login(member);
            Map<String,String> result = new HashMap<>();
            result.put("Authorization",token);
            return ResponseEntity.ok(result);
        }catch (IllegalStateException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
    @PostMapping("")
    public ResponseEntity<?> join(@RequestBody Member member){
        try {
            memberService.join(member);
            return ResponseEntity.ok().build();
        }catch (DuplicateMemberException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @GetMapping("/myinfo")
    public ResponseEntity<?> getMyInfo( @RequestHeader("Authorization") String token){
        try {
            String email = JwtBuilder.getEmailFromJwt(token);
            return ResponseEntity.ok(memberService.getMemberByEmail(email));
        }catch (NoMatchedMemberException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/myinfo/goals/{state}/{page:[0-9]+}")
    public ResponseEntity<?> getMyGoals(@RequestHeader("Authorization") String token, @PathVariable String state,@PathVariable int page){
        try {
            String email = JwtBuilder.getEmailFromJwt(token);
            return ResponseEntity.ok(goalService.getGoalsByEmailAndState(email, state, page));
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/myinfo/notifications")
    public ResponseEntity<?> getMyGoals(@RequestHeader("Authorization") String token){
        try {
            String email = JwtBuilder.getEmailFromJwt(token);
            return ResponseEntity.ok(notificationService.getNotificationsByEmail(email));
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PutMapping("/myinfo")
    public ResponseEntity<?> updateMyInfo(@RequestBody Member member, @RequestHeader("Authorization") String token){
        try {
            String email = JwtBuilder.getEmailFromJwt(token);
            memberService.updateMember(member, email);
            return ResponseEntity.ok(memberService.getMemberByEmail(email));
        }catch (NoMatchedMemberException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
