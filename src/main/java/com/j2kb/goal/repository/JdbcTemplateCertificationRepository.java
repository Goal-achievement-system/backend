package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Certification;
import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.GoalState;
import com.j2kb.goal.exception.DuplicateCertificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
@Repository
public class JdbcTemplateCertificationRepository implements CertificationRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateCertificationRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertCertification(Certification certification) {
        String sql = "insert into certification(goal_id,content,image,require_success_count) values(?,?,?,?)";
        try{
            jdbcTemplate.update(sql,certification.getGoalId(),certification.getContent(),certification.getImage(),certification.getRequireSuccessCount());
        }catch (DataAccessException e){
            throw new DuplicateCertificationException(HttpStatus.CONFLICT, ErrorCode.CERTIFICATION_ALREADY_EXISTS, "POST /apis/goals/cert/"+certification.getGoalId(), "The Certification already exists.");
        }
    }

    @Override
    public List<Certification> selectUnVerifiedCertifications() {
        String sql = "select * from certification where verification_result = 'ongoing'";
        return jdbcTemplate.query(sql, new CertificationRowMapper<Certification>());
    }

    @Override
    public List<Certification> selectMembersCertificationByEmail(String email, GoalState goalState, int page) {
        int start = (page-1) * 6;
        String sql = "select * from certification where goal_id = (select goal_id from goal where member_email = ? and verification_result = ?) limit ?,?";
        if(goalState.equals(GoalState.all)){
            sql = sql.replace("verification_result = ?","verification_result != ?");
        }
        return jdbcTemplate.query(sql,new CertificationRowMapper<>(),email,goalState.name(),start,6);
    }

    @Override
    public Optional<Certification> selectCertificationByGoalId(long goalId) {
        String sql = "select * from certification where goal_id = ?";
        Certification certification;
        certification = jdbcTemplate.queryForObject(sql, new CertificationRowMapper<Certification>(), goalId);
        return Optional.ofNullable(certification);
    }

    @Override
    public int selectMembersCertificationsMaxPageByEmail(String email, GoalState goalState) {
        String sql = "select count(*) from certification where goal_id = (select goal_id from goal where member_email = ? and verification_result = ?)";
        if(goalState.equals(GoalState.all)){
            sql = sql.replace("verification_result = ?","verification_result != ?");
        }
        return jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        },email,goalState.name()).get(0);
    }

    @Override
    public void deleteCertification(Certification certification) {
        String sql = "delete certification where cert_id = ?";
        jdbcTemplate.update(sql,certification.getCertId());
    }

    @Override
    public void increaseSuccessCount(long goalId) {
        String sql = "update certification set success_count = success_count+1," +
                "verification_result = case when require_success_count > success_count then 'state1' else 'state2' end " +
                "where goal_id = ?";
        sql = sql.replace("state1", GoalState.oncertification.name());
        sql = sql.replace("state2", GoalState.success.name());
        jdbcTemplate.update(sql,goalId);
    }

    @Override
    public void increaseFailCount(long goalId) {
        String sql = "update certification set fail_count = fail_count+1," +
                "verification_result = case when require_success_count * 0.9 > fail_count then 'state1' else 'state2' end " +
                "where goal_id = ?";
        sql = sql.replace("state1", GoalState.oncertification.name());
        sql = sql.replace("state2", GoalState.hold.name());
        jdbcTemplate.update(sql,goalId);
    }


    private class CertificationRowMapper<T extends Certification> implements RowMapper<T>{

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            Certification.CertificationBuilder builder = Certification.builder();
            builder.certId(rs.getLong("cert_id"))
                    .goalId(rs.getLong("goal_id"))
                    .content(rs.getString("content"))
                    .image(rs.getString("image"))
                    .requireSuccessCount(rs.getByte("require_success_count"))
                    .successCount(rs.getByte("success_count"))
                    .failCount(rs.getByte("fail_count"))
                    .verificationResult(rs.getString("verification_result"));
            return (T) builder.build();
        }
    }
}
