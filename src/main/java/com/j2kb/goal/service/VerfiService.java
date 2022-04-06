package com.j2kb.goal.service;

import com.j2kb.goal.dto.Goal;
import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.repository.CertificationRepository;
import com.j2kb.goal.repository.GoalRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class VerfiService implements AbstractVerfiService{
    private CertificationRepository certificationRepository;
    private GoalRepository goalRepository;
    public VerfiService(CertificationRepository certificationRepository,GoalRepository goalRepository){
        this.certificationRepository = certificationRepository;
        this.goalRepository = goalRepository;
    }
    @Override
    public void success(long goalId,String goalOwnerEmail) {
        Goal goal = goalRepository.selectGoalByGoalId(goalId).get();
        if(canVerfication(goal,goalOwnerEmail))
        try{
            certificationRepository.increaseSuccessCount(goalId);
        }catch (DataAccessException e){
            throw new NoMatchedCertificationException("cert whith goalId = "+goalId+" is not exist",e);
        }
    }

    @Override
    public void fail(long goalId,String goalOwnerEmail) {
        try{
            certificationRepository.increaseFailCount(goalId);
        }catch (DataAccessException e){
            throw new NoMatchedCertificationException("cert whith goalId = "+goalId+" is not exist",e);
        }
    }

    private boolean canVerfication(Goal goal, String goalOwnerEmail){
        return !goal.getMemberEmail().contentEquals(goalOwnerEmail);
    }
}
