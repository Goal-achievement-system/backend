package com.j2kb.goal.service;

import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.DuplicateMemberException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.MemberRepository;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MemberService implements AbstractMemberService{

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public String login(Member member) {
        try {
            if (memberRepository.login(member)) {
                return JwtBuilder.build(member.getEmail());
            } else {
                throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.LOGIN_FAIL,"POST /api/members/login","email or password is wrong");
            }
        }catch (DataAccessException e){
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.LOGIN_FAIL,"POST /api/members/login","email or password is wrong");
        }
    }

    @Override
    public void join(Member member) {
        try{
            memberRepository.insertMember(member);
        }catch (DataAccessException e){
            throw new DuplicateMemberException(HttpStatus.CONFLICT, ErrorCode.DUPLICATED_EMAIL, "POST /api/members", "Member with email = "+member.getEmail() + "is exist");
        }
    }

    @Override
    public boolean canJoin(String email) {
        try{
            memberRepository.selectMemberByMemberEmail(email);
        }catch (IndexOutOfBoundsException e){
            return true;
        }
        return false;
    }

    @Override
    public Member getMemberByEmail(String email) {
        Member member;
        try{
            return memberRepository.selectMemberByMemberEmail(email);
        }catch (DataAccessException | NullPointerException e){
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.INVALID_TOKEN,"GET /api/members/myinfo","Member with email = "+email+ "is not exist");
        }
    }

    @Override
    public void updateMember(Member member, String memberEmail) {
        if (member.getEmail().contentEquals(memberEmail)) {
            try {
                memberRepository.updateMember(member);
            } catch (DataAccessException e) {
                throw new NoMatchedMemberException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorCode.UNKNOWN,"PUT /api/members/myinfo","Member with email = "+memberEmail+ "is not exist");
            }
        }else{
            throw new PermissionException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED, "PUT /api/members/myinfo", "can not access other Member");
        }
    }

    @Override
    public void updatePassword(Map<String, String> passwords, String memberEmail) {
        String nowPassword = passwords.get("password");
        String nowPasswordRepeat = passwords.get("repeat");
        if(nowPassword.contentEquals(nowPasswordRepeat)){
            try {
                memberRepository.updateMemberPassword(passwords, memberEmail);
            }catch (DataAccessException e){
                throw new SpringHandledException(HttpStatus.UNAUTHORIZED,ErrorCode.LOGIN_FAIL,"PUT /api/members/myinfo/password", "wrong password");
            }
        }else{
            throw new PermissionException(HttpStatus.UNAUTHORIZED,ErrorCode.PERMISSION_DENIED,"PUT /api/members/myinfo/password", "password and repeat is not equal");
        }
    }

    @Override
    public void withdrawal(Member member) {
        try {
            memberRepository.deleteMember(member);
        }catch (DataAccessException e){
            throw new PermissionException(HttpStatus.FORBIDDEN, ErrorCode.PERMISSION_DENIED, "DELETE /api/members/myinfo/withdrawal", "password fail");
        }
    }

    @Override
    public void chargeMoney(Member member) {
        if(memberRepository.login(member)){
            memberRepository.plusMoney(member,member.getMoney());
        }else{
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.PERMISSION_DENIED,"PUT /api/members/myinfo/charge","email or password is wrong");
        }
    }

    @Override
    public void refundMoney(Member member) {
        if(memberRepository.login(member)){
            memberRepository.minusMoney(member,member.getMoney());
        }else{
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.PERMISSION_DENIED,"PUT /api/members/myinfo/refund","email or password is wrong");
        }
    }
}
