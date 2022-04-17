package com.j2kb.goal.service;

import com.j2kb.goal.dto.Member;

public interface AbstractMemberService {
    String login(Member member);
    void join(Member member);
    Member getMemberByEmail(String email);
    void updateMember(Member member,String memberEmail);
    void withdrawal(Member member);
    void chargeMoney(Member member);
    void refundMoney(Member member);
}
