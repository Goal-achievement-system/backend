package com.j2kb.goal.service;

import com.j2kb.goal.exception.NoMatchedCertificationException;
import com.j2kb.goal.repository.CertificationRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class VerfiService implements AbstractVerfiService{
    private CertificationRepository certificationRepository;
    public VerfiService(CertificationRepository certificationRepository){
        this.certificationRepository = certificationRepository;
    }
    @Override
    public void success(long goalId) {
        try{
            certificationRepository.increaseSuccessCount(goalId);
        }catch (DataAccessException e){
            throw new NoMatchedCertificationException("cert whith goalId = "+goalId+" is not exist",e);
        }
    }

    @Override
    public void fail(long goalId) {
        try{
            certificationRepository.increaseFailCount(goalId);
        }catch (DataAccessException e){
            throw new NoMatchedCertificationException("cert whith goalId = "+goalId+" is not exist",e);
        }
    }
}
