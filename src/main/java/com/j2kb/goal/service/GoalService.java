package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.MoneyOverflowException;
import com.j2kb.goal.exception.NoMatchedCategoryException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.exception.PermissionException;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
    public Optional<Goal> getGoalByGoalId(long goalId) {
        return goalRepository.selectGoalByGoalId(goalId);
    }
    @Transactional
    @Override
    public Goal addGoal(Goal goal, String email) {
        if(!email.contentEquals(goal.getMemberEmail())){
            throw new PermissionException("adding other member's goal is impossible");
        }
        List<String> categories = goalRepository.selectAllCategories();
        if(!categories.contains(goal.getCategory())){
            throw new NoMatchedCategoryException(goal.getCategory()+" is not found");
        }
        Member member = memberRepository.selectMemberByMemberEmail(goal.getMemberEmail());
        if(member.getMoney()>=goal.getMoney()){
            memberRepository.minusMoney(member,goal.getMoney());
            return goalRepository.insertGoal(goal);
        }else{
            throw new MoneyOverflowException("money overflow");
        }
    }

    @Override
    public List<Goal> getGoalsByCategoryAndState(String category, String state, int page) {
        List<Goal> result = Collections.emptyList();
        List<String> categories = goalRepository.selectAllCategories();
        if(!categories.contains(category)){
            throw new NoMatchedCategoryException(category + "is not exist");
        }
        switch (state){
            case "all":
                result = goalRepository.selectAllGoalsByCategory(category,page);
                break;
            case "success":
                result = goalRepository.selectSuccessGoalsByCategory(category,page);
                break;
            case "fail" :
                result = goalRepository.selectFailGoalsByCategory(category,page);
                break;
            case "ongoing":
                result = goalRepository.selectOnGoingGoalsByCategory(category,page);
                break;
            case  "hold":
                result = goalRepository.selectHoldGoalsByCategory(category,page);
                break;
            default:
                throw new IllegalArgumentException("illegal State");
        }
        return result;
    }

    @Override
    public List<Goal> getGoalsByEmailAndState(String email, String state,int page) {
        List<Goal> result = Collections.emptyList();
        try{
            Member member = memberRepository.selectMemberByMemberEmail(email);
        }catch (DataAccessException e){
            throw new NoMatchedMemberException("member with "+email +" is not exist",e);
        }

        switch (state){
            case "all":
                result = goalRepository.selectAllGoalsByEmail(email,page);
                break;
            case "success":
                result = goalRepository.selectSuccessGoalsByEmail(email,page);
                break;
            case "fail" :
                result = goalRepository.selectFailGoalsByEmail(email,page);
                break;
            case "ongoing":
                result = goalRepository.selectOnGoingGoalsByEmail(email,page);
                break;
            case  "hold":
                result = goalRepository.selectHoldGoalsByEmail(email,page);
                break;
            default:
                throw new IllegalArgumentException("illegal State");
        }
        return result;
    }

    @Override
    public List<String> getCategories() {
        return goalRepository.selectAllCategories();
    }

}
