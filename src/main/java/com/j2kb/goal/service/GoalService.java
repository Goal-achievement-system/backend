package com.j2kb.goal.service;

import com.j2kb.goal.dto.*;
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
    public GoalsWithPagination getGoalsByCategoryAndState(String category, GoalState state, int page) {
        List<Goal> goals = Collections.emptyList();
        List<String> categories = goalRepository.selectAllCategories();
        categories.add("all");
        if(!categories.contains(category)){
            throw new NoMatchedCategoryException(HttpStatus.NOT_FOUND, ErrorCode.INVALID_CATEGORY, "GET /api/goals/list/"+state.name()+"/"+page, category + " category is not exist");
        }
        goals = goalRepository.selectGoalsByCategoryAndState(category,state,page);
        long countOfGoals = goalRepository.selectCountOfGoalsByCategoryAndState(category,state);
        long maxPage = countOfGoals / GoalRepository.GOAL_COUNT;
        if(countOfGoals%GoalRepository.GOAL_COUNT!=0){
            maxPage += 1;
        }
        GoalsWithPagination result = new GoalsWithPagination(maxPage,goals);
        return result;
    }

    @Override
    public GoalsWithPagination getGoalsByEmailAndState(String email, GoalState state, int page) {
        List<Goal> goals = Collections.emptyList();
        try{
            Member member = memberRepository.selectMemberByMemberEmail(email);
        }catch (DataAccessException e){
            throw new NoMatchedMemberException(HttpStatus.UNAUTHORIZED,ErrorCode.INVALID_TOKEN,"GET /api/members/myinfo/goals/"+state.name()+"/"+page,"member with "+email +" is not exist");
        }
        goals = goalRepository.selectGoalsByEmailAndState(email,state,page);
        long countOfGoals = goalRepository.selectCountOfGoalsByEmailAndState(email,state);
        long maxPage = countOfGoals / GoalRepository.MYINFO_GOAL_COUNT;
        if(countOfGoals%GoalRepository.MYINFO_GOAL_COUNT!=0){
            maxPage += 1;
        }
        GoalsWithPagination result = new GoalsWithPagination(maxPage,goals);
        return result;
    }

    @Override
    public GoalsWithPagination getOnCertificationGoalsByCategory(String category, int page) {
        List<Goal> goals = Collections.emptyList();
        try {
            goals =  goalRepository.selectGoalsByCategoryAndState(category, GoalState.oncertification,page);
        }catch (DataAccessException e){
            throw new NoMatchedCategoryException(HttpStatus.NOT_FOUND,ErrorCode.INVALID_CATEGORY,"GET /api/goals/"+category+"/list/"+page,category + " category is not exist");
        }
        GoalsWithPagination result = new GoalsWithPagination(1,goals);
        return result;
    }

    @Override
    public List<String> getCategories() {
        return goalRepository.selectAllCategories();
    }

}
