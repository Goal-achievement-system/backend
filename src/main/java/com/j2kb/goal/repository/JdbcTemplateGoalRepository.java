package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateGoalRepository implements GoalRepository{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateGoalRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<String> selectAllCategories() {
        String sql = "select category from category_list";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("category"));
    }

    @Override
    public Goal insertGoal(Goal goal) {
        String sql = "insert into goal(member_email,category,goal_name,content,limit_date,money,reward) values(?,?,?,?,?,?,?)";
        //jdbcTemplate.update(sql, goal.getMemberEmail(),goal.getCategory(),goal.getGoalName(),goal.getContent(),goal.getLimitDate(),goal.getMoney(),goal.getReward());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"goal_id"});
            preparedStatement.setString(1,goal.getMemberEmail());
            preparedStatement.setString(2, goal.getCategory());
            preparedStatement.setString(3,goal.getGoalName());
            preparedStatement.setString(4,goal.getContent());
            preparedStatement.setTimestamp(5,goal.getLimitDate());
            preparedStatement.setInt(6,goal.getMoney());
            preparedStatement.setString(7,goal.getReward());
            return preparedStatement;
        },keyHolder);
        long goalId = keyHolder.getKey().longValue();
        Goal result = selectGoalByGoalId(goalId).orElse(Goal.builder().build());
        return result;
    }

    @Override
    public Optional<Goal> selectGoalByGoalId(long goalId) {
        String sql = "select * from goal where goal_id = ?";
        Goal goal;
        try {
            goal = jdbcTemplate.queryForObject(sql, new GoalRowMapper<Goal>(), goalId);
        }catch (Exception e){
            goal = null;
        }
        Optional<Goal> ret = Optional.ofNullable(goal);
        return ret;
    }

    @Override
    public Optional<Goal> selectGoalByGoalId(long goalId, RowMapper<Goal> rowMapper) {
        String sql = "select * from goal where goal_id = ?";
        Goal goal;
        try {
            goal = jdbcTemplate.queryForObject(sql, rowMapper, goalId);
        }catch (Exception e){
            goal = null;
        }
        Optional<Goal> ret = Optional.ofNullable(goal);
        return ret;
    }

    @Override
    public List<Goal> selectAllGoalsByEmail(String email) {
        String sql = "select * from goal where member_email = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),email);
        return results;
    }

    @Override
    public List<Goal> selectFailGoalsByEmail(String email) {
        String sql = "select * from goal where verification_result = 'fail' and member_email = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),email);
        return results;
    }

    @Override
    public List<Goal> selectSuccessGoalsByEmail(String email) {
        String sql = "select * from goal where verification_result = 'success' and member_email = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),email);
        return results;
    }

    @Override
    public List<Goal> selectOnGoingGoalsByEmail(String email) {
        String sql = "select * from goal where verification_result = 'ongoing' and member_email = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),email);
        return results;
    }

    @Override
    public List<Goal> selectHoldGoalsByEmail(String email) {
        String sql = "select * from goal where verification_result = 'hold' and member_email = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),email);
        return results;
    }

    @Override
    public List<Goal> selectAllGoalsByCategory(String category) {
        String sql = "select * from goal where category = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),category);
        return results;
    }

    @Override
    public List<Goal> selectFailGoalsByCategory(String category) {
        String sql = "select * from goal where verification_result = 'fail' and category = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),category);
        return results;
    }

    @Override
    public List<Goal> selectSuccessGoalsByCategory(String category) {
        String sql = "select * from goal where verification_result = 'success' and category = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),category);
        return results;
    }

    @Override
    public List<Goal> selectOnGoingGoalsByCategory(String category) {
        String sql = "select * from goal where verification_result = 'ongoing' and category = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),category);
        return results;
    }

    @Override
    public List<Goal> selectHoldGoalsByCategory(String category) {
        String sql = "select * from goal where verification_result = 'hold' and category = ?";
        List<Goal> results = jdbcTemplate.query(sql,new GoalRowMapper<Goal>(),category);
        return results;
    }

    @Override
    public long selectAllGoalsCount() {
        String sql = "select count(*) from goal";
        Long result = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong(1));
        return result;
    }

    @Override
    public long selectAllSuccessGoalsCount() {
        String sql = "select count(*) from goal where verification_result = 'success'";
        Long result = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong(1));
        return result;
    }

    @Override
    public long selectAllFailGoalsCount() {
        String sql = "select count(*) from goal where verification_result = 'fail'";
        Long result = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong(1));
        return result;
    }

    @Override
    public long selectAllOngoingGoalsCount() {
        String sql = "select count(*) from goal where verification_result = 'ongoing'";
        Long result = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong(1));
        return result;
    }


    private class GoalRowMapper<T extends Goal> implements RowMapper<T>{
        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            Goal.GoalBuilder goalBuilder = Goal.builder();
            goalBuilder.goalId(rs.getLong("goal_id"))
                    .category(rs.getString("category"))
                    .goalName(rs.getString("goal_name"))
                    .content(rs.getString("content"))
                    .limitDate(rs.getTimestamp("limit_date"))
                    .money(rs.getInt("money"))
                    .reward(rs.getString("reward"))
                    .verificationResult(rs.getString("verification_result"));
            return (T)goalBuilder.build();
        }
    }
}
