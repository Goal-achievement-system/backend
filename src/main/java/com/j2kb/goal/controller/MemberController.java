package com.j2kb.goal.controller;

import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.DuplicateMemberException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.service.*;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private AbstractMemberService memberService;
    private AbstractNotificationService notificationService;
    private AbstractGoalService goalService;

    @Autowired
    public MemberController(MemberService memberService, NotificationService notificationService, GoalService goalService) {
        this.memberService = memberService;
        this.notificationService = notificationService;
        this.goalService = goalService;
    }
    @GetMapping("/check/email/{email:.+}")
    public ResponseEntity<?> canJoin(@PathVariable String email){
        String regx  = "[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return ResponseEntity.ok(memberService.canJoin(email));
        }else{
            return ResponseEntity.ok(false);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member){
        String token = memberService.login(member);
        Map<String,String> result = new HashMap<>();
        result.put("Authorization",token);
        return ResponseEntity.ok(result);
    }
    @PostMapping("")
    public ResponseEntity<?> join(@RequestBody Member member){
        memberService.join(member);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/myinfo")
    public ResponseEntity<?> getMyInfo( @RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        return ResponseEntity.ok(memberService.getMemberByEmail(email));
    }
    @GetMapping("/myinfo/goals/{state}/{page:[0-9]+}")
    public ResponseEntity<?> getMyGoals(@RequestHeader("Authorization") String token, @PathVariable GoalState state, @PathVariable int page){
        String email = JwtBuilder.getEmailFromJwt(token);
        return ResponseEntity.ok(goalService.getGoalsByEmailAndState(email, state, page));
    }
    @GetMapping("/myinfo/notifications")
    public ResponseEntity<?> getMyNotifications(@RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        return ResponseEntity.ok(notificationService.getNotificationsByEmail(email));
    }
    @PutMapping("/myinfo")
    public ResponseEntity<?> updateMyInfo(@RequestBody Member member, @RequestHeader("Authorization") String token){
        String email = JwtBuilder.getEmailFromJwt(token);
        memberService.updateMember(member, email);
        return ResponseEntity.ok(memberService.getMemberByEmail(email));
    }
    @PutMapping("/myinfo/charge")
    public ResponseEntity<?> chargeMoney(@RequestHeader("Authorization") String token, @RequestBody Member member){
        memberService.chargeMoney(member);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/myinfo/refund")
    public ResponseEntity<?> refundMoney(@RequestHeader("Authorization") String token, @RequestBody Member member){
        memberService.refundMoney(member);
        return ResponseEntity.ok().build();
    }
}
