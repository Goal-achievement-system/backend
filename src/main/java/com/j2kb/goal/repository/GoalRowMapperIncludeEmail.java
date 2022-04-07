package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Goal;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalRowMapperIncludeEmail implements RowMapper<Goal> {
    @Override
    public Goal mapRow(ResultSet rs, int rowNum) throws SQLException {
        Goal.GoalBuilder goalBuilder = Goal.builder();
        goalBuilder.goalId(rs.getLong("goal_id"))
                .memberEmail(rs.getString("member_email"))
                .category(rs.getString("category"))
                .goalName(rs.getString("goal_name"))
                .content(rs.getString("content"))
                .limitDate(rs.getTimestamp("limit_date"))
                .money(rs.getInt("money"))
                .reward(rs.getString("reward"))
                .verificationResult(rs.getString("verification_result"));
        return goalBuilder.build();
    }
}
