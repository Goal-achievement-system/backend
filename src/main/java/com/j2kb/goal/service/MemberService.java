package com.j2kb.goal.service;

import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.DuplicateMemberException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.repository.MemberRepository;
import com.j2kb.goal.util.JwtBuilder;
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
        if(memberRepository.login(member)){
            return JwtBuilder.build(member.getEmail());
        }else{
            return "";
        }
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
    public void updateMember(Member member, String memberEmail) {
        if (member.getEmail().contentEquals(memberEmail)) {
            try {
                memberRepository.updateMember(member);
            } catch (DataAccessException e) {
                throw new DuplicateMemberException("Member with email = " + member.getEmail() + "is not exist", e);
            }
        }else{
            throw new RuntimeException("can not access other Member");
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
