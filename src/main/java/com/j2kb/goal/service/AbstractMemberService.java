package com.j2kb.goal.service;

import com.j2kb.goal.dto.Member;

import java.util.Map;

public interface AbstractMemberService {
    String login(Member member);
    void join(Member member);
    boolean canJoin(String email);
    Member getMemberByEmail(String email);
    void updateMember(Member member,String memberEmail);
    void updatePassword(Map<String,String> passwords, String memberEmail);
    void withdrawal(Member member);
    void chargeMoney(Member member);
    void refundMoney(Member member);
}
