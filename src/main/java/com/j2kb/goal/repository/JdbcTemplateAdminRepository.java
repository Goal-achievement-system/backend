package com.j2kb.goal.repository;

import com.j2kb.goal.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class JdbcTemplateAdminRepository implements AdminRepository{
    private static final int GOAL_COUNT = 9;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public JdbcTemplateAdminRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean login(Admin admin) {
        return false;
    }

    @Override
    public List<GoalAndCert> selectHoldGoalAndCerts(int page) {
        int start = (page-1) * GOAL_COUNT;
        String sql = "select goal.*, cert_id, certification.content AS cert_content ,certification.image from goal INNER JOIN certification ON goal.verification_result = 'hold' and goal.goal_id = certification.goal_id limit ?,?";
        return jdbcTemplate.query(sql,new GoalAndCertRowMapper<GoalAndCert>(),start,GOAL_COUNT);
    }

    @Override
    public void updateGoalVerificationResult(Goal goal) {
        String sql = "update goal set verification_result = ? where goal_id = ?";
        jdbcTemplate.update(sql,goal.getVerificationResult(),goal.getGoalId());
    }

    @Override
    public void insertAnnouncement(Announcement announcement) {
        String sql = "insert into announcement(title,description,content) values(?,?,?)";
        jdbcTemplate.update(sql,announcement.getTitle(),announcement.getDescription(),announcement.getContent());
    }

    class GoalAndCertRowMapper<T extends GoalAndCert> implements RowMapper<T>{

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
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
            Goal goal = goalBuilder.build();
            Certification.builder().certId(rs.getLong("cert_id"))
                    .content("cert_content")
                    .image("image");
            Certification certification = Certification.builder().build();
            return (T) new GoalAndCert(goal,certification);
        }
    }
}
