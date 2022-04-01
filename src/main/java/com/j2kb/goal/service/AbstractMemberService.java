package com.j2kb.goal.service;

import com.j2kb.goal.dto.Member;

public interface AbstractMemberService {
    String login(Member member);
    void join(Member member);
    Member getMemberByEmail(String email);
    void updateMember(Member member);
    void withdrawal(Member member);
}
