package com.j2kb.goal.service;

import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.DuplicateMemberException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class MemberService implements AbstractMemberService{

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public String login(Member member) {
        return null;
    }

    @Override
    public void join(Member member) {
        try{
            memberRepository.insertMember(member);
        }catch (DataAccessException e){
            throw new DuplicateMemberException("Member with email = "+member.getEmail() + "is exist",e);
        }
    }

    @Override
    public Member getMemberByEmail(String email) {
        Member member;
        try{
            return memberRepository.selectMemberByMemberEmail(email);
        }catch (DataAccessException | NullPointerException e){
            throw new NoMatchedMemberException("Member with email = "+email+ "is not exist",e);
        }
    }

    @Override
    public void updateMember(Member member) {
        try {
            memberRepository.updateMember(member);
        }catch (DataAccessException e){
            throw new DuplicateMemberException("Member with email = "+member.getEmail() + "is not exist",e);
        }
    }

    @Override
    public void withdrawal(Member member) {
        try {
            memberRepository.deleteMember(member);
        }catch (DataAccessException e){
            throw new DuplicateMemberException("Member with email = "+member.getEmail() + "is not exist",e);
        }
    }
}
