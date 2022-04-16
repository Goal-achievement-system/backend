package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;

public interface AbstractVerfiService {
    void success(long goalId, String requestEmail);
    void fail(long goalId,String requestEmail);
}
