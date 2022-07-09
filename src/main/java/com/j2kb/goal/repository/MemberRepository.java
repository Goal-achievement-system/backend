package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Member;

import java.util.Map;

public interface MemberRepository {
    void insertMember(Member member);
    Member selectMemberByMemberEmail(String memberEmail);
    void minusMoney(Member member, int money);
    void plusMoney(Member member, int money);
    void updateMember(Member member);
    void updateMemberPassword(Map<String,String> passwords, String memberEmail);
    void deleteMember(Member member);
    boolean login(Member member);
}
