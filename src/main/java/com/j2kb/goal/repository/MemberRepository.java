package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Member;

public interface MemberRepository {
    void insertMember(Member member);
    Member selectMemberByMemberId(String MemberId);
    void updateMember(Member member);
    void deleteMember(Member member);
}
