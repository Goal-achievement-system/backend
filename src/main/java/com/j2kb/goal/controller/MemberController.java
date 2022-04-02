package com.j2kb.goal.controller;

import com.j2kb.goal.dto.Member;
import com.j2kb.goal.service.MemberService;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Member member){
        String token = memberService.login(member);
        Map<String,String> result = new HashMap<>();
        result.put("Authorization",token);
        return result;
    }
    @PostMapping("/")
    public void join(@RequestBody Member member){
        memberService.join(member);
    }
    @GetMapping("/myinfo")
    public Member getMyInfo(){
        String email = ""; // ToDo "인증토큰으로부터 이메일 뽑아내는 로직 추가 필요"
        return memberService.getMemberByEmail(email);
    }
    @PutMapping("/myinfo")
    public Member updateMyInfo(@RequestBody Member member){
        String email = ""; // ToDo "인증토큰으로부터 이메일 뽑아내는 로직 추가 필요"
        memberService.updateMember(member);
        return memberService.getMemberByEmail(email);
    }
    @PostMapping("/verfi")
    public boolean f(@RequestBody Map<String,String> map){
        return JwtBuilder.isValid(map.get("Authorization"));
    }
}
