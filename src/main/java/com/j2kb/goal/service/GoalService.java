package com.j2kb.goal.service;

import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.*;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GoalService implements AbstractGoalService{
    private GoalRepository goalRepository;
    private MemberRepository memberRepository;
    @Autowired
    public GoalService(GoalRepository goalRepository, MemberRepository memberRepository){
        this.goalRepository = goalRepository;
        this.memberRepository = memberRepository;
    }
    @Override
    public Goal getGoalByGoalId(long goalId) {
        try {
            return goalRepository.selectGoalByGoalId(goalId).orElseThrow();
        }catch (DataAccessException | NoSuchElementException e){
            throw new NoMatchedGoalException(HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND,"GET /api/goals/"+goalId,"goal with goalId = "+goalId+" is not found");
        }
    }
    @Transactional
    @Override
    public Goal addGoal(Goal goal, String email) {
        if(!email.contentEquals(goal.getMemberEmail())){
            throw new PermissionException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED, "POST /api/goals", "adding other member's goal is impossible");
        }
        List<String> categories = goalRepository.selectAllCategories();
        if(!categories.contains(goal.getCategory())){
            throw new NoMatchedCategoryException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_CATEGORY, "POST /api/goals", goal.getCategory()+" category is not found");
        }
        Member member = memberRepository.selectMemberByMemberEmail(goal.getMemberEmail());
        if(member.getMoney()>=goal.getMoney()){
            memberRepository.minusMoney(member,goal.getMoney());
            return goalRepository.insertGoal(goal);
        }else{
            throw new MoneyOverflowException(HttpStatus.FORBIDDEN, ErrorCode.LACK_OF_MONEY, "POST /api/goals", "lack of money");
        }
    }

    @Override
    public List<Goal> getGoalsByCategoryAndState(String category, GoalState state, int page) {
        List<Goal> result = Collections.emptyList();
        List<String> categories = goalRepository.selectAllCategories();
        categories.add("all");
        if(!categories.contains(category)){
            throw new NoMatchedCategoryException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_CATEGORY, "GET /api/goals/list/"+state.name()+"/"+page, category + " category is not exist");
        }
        result = goalRepository.selectGoalsByCategoryAndState(category,state,page);
        return result;
    }

    @Override
    public List<Goal> getGoalsByEmailAndState(String email, GoalState state,int page) {
        List<Goal> result = Collections.emptyList();
        try{
            Member member = memberRepository.selectMemberByMemberEmail(email);
        }catch (DataAccessException e){
            throw new NoMatchedMemberException("member with "+email +" is not exist",e);
        }
        result = goalRepository.selectGoalsByEmailAndState(email,state,page);
        return result;
    }

    @Override
    public List<Goal> getOnCertificationGoalsByCategory(String category, int page) {
        try {
            return goalRepository.selectGoalsByCategoryAndState(category, GoalState.oncertification,page);
        }catch (DataAccessException e){
            throw new NoMatchedCategoryException(HttpStatus.NOT_FOUND,ErrorCode.INVALID_CATEGORY,"GET /api/goals/"+category+"/list/"+page,category + " category is not exist");
        }
    }

    @Override
    public List<String> getCategories() {
        return goalRepository.selectAllCategories();
    }

}
