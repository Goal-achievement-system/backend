package com.j2kb.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@AllArgsConstructor
@Getter
public class GoalsWithPagination {
    private long maxPage;
    private List<Goal> goals;
}
