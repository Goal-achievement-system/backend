package com.j2kb.goal.service;

import com.j2kb.goal.dto.Certification;

public interface AbstractVerfiService {
    boolean success(long goalId, String goalOwnerEmail);
    boolean fail(long goalId,String goalOwnerEmail);
}
