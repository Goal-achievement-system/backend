package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.exception.DuplicateCertificationException;
import com.j2kb.goal.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CertService implements AbstractCertService{
    private CertificationRepository certificationRepository;

    @Autowired
    public CertService(CertificationRepository certificationRepository){
        this.certificationRepository = certificationRepository;
    }

    @Override
    public boolean addCert(Certification certification) {
        try {
            certificationRepository.insertCertification(certification);
        }catch (DuplicateCertificationException e){
            return false;
        }
        return true;
    }

    @Override
    public Optional<Certification> getCertificationByGoalId(long goalId) {
        return certificationRepository.selectCertificationByGoalId(goalId);
    }
}
