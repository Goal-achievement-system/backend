package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.NoMatchedCategoryException;
import com.j2kb.goal.exception.NoMatchedMemberException;
import com.j2kb.goal.repository.GoalRepository;
import com.j2kb.goal.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Goal addGoal(Goal goal) {
        Member member = memberRepository.selectMemberByMemberEmail(goal.getMemberEmail());
        if(member.getMoney()>=goal.getMoney()){
            memberRepository.minusMoney(member,goal.getMoney());
            return goalRepository.insertGoal(goal);
        }else{
            return Goal.builder().build();
        }
    }

    @Override
    public List<Goal> getGoalsByCategoryAndState(String category, String state) {
        List<Goal> result = Collections.emptyList();
        List<String> categories = goalRepository.selectAllCategories();
        if(!categories.contains(category)){
            throw new NoMatchedCategoryException(category + "is not exist");
        }
        switch (state){
            case "all":
                result = goalRepository.selectAllGoalsByCategory(category);
                break;
            case "success":
                result = goalRepository.selectSuccessGoalsByCategory(category);
                break;
            case "fail" :
                result = goalRepository.selectFailGoalsByCategory(category);
                break;
            case "ongoing":
                result = goalRepository.selectOnGoingGoalsByCategory(category);
                break;
            case  "hold":
                result = goalRepository.selectHoldGoalsByCategory(category);
                break;
            default:
                throw new IllegalArgumentException("illegal State");
        }
        return result;
    }

    @Override
    public List<Goal> getGoalsByEmailAndState(String email, String state) {
        List<Goal> result = Collections.emptyList();
        try{
            Member member = memberRepository.selectMemberByMemberEmail(email);
        }catch (IllegalStateException e){
            throw new NoMatchedMemberException("member with "+email +" is not exist",e);
        }

        switch (state){
            case "all":
                result = goalRepository.selectAllGoalsByEmail(email);
                break;
            case "success":
                result = goalRepository.selectSuccessGoalsByEmail(email);
                break;
            case "fail" :
                result = goalRepository.selectFailGoalsByEmail(email);
                break;
            case "ongoing":
                result = goalRepository.selectOnGoingGoalsByEmail(email);
                break;
            case  "hold":
                result = goalRepository.selectHoldGoalsByEmail(email);
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
