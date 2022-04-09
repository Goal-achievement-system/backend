package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Member;

public interface MemberRepository {
    void insertMember(Member member);
    Member selectMemberByMemberEmail(String memberEmail);
    void minusMoney(Member member, int money);
    void plusMoney(Member member, int money);
    void updateMember(Member member);
    void deleteMember(Member member);
    boolean login(Member member);
}
