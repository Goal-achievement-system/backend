package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Certification;
import org.springframework.beans.factory.annotation.Autowired;
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
        String sql = "insert into certification(goal_id,content,image,require_success_count,success_count,fail_count) values(?,?,?,?,?,?)";
        jdbcTemplate.update(sql,certification.getGoalId(),certification.getContent(),certification.getImage(),certification.getRequireSuccessCount(),certification.getSuccessCount(),certification.getFailCount());
    }

    @Override
    public List<Certification> selectUnVerifiedCertifications() {
        String sql = "select * from certification where verification_result = 'ongoing'";
        return jdbcTemplate.query(sql, new CertificationRowMapper<Certification>());
    }

    @Override
    public Optional<Certification> selectCertificationByGoalId(long goalId) {
        String sql = "select * from certification where goal_id = ?";
        Certification certification;
        try {
            certification = jdbcTemplate.queryForObject(sql, new CertificationRowMapper<Certification>(), goalId);
        }catch (Exception e){
            certification = null;
        }
        return Optional.ofNullable(certification);
    }

    @Override
    public void deleteCertification(Certification certification) {
        String sql = "delete certification where cert_id = ?";
        jdbcTemplate.update(sql,certification.getCertId());
    }

    @Override
    public void increaseSuccessCount(long certId) {
        String sql = "update certification set success_count = success_count+1," +
                "verification_result = case when require_success_count > success_count then 'ongoing' else 'success' end " +
                "where cert_id = ?";
        jdbcTemplate.update(sql,certId);
    }

    @Override
    public void increaseFailCount(long certId) {
        String sql = "update certification set fail_count = fail_count+1," +
                "verification_result = case when require_success_count * 0.9 > fail_count then 'ongoing' else 'hold' end " +
                "where cert_id = ?";
        jdbcTemplate.update(sql,certId);
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
                    .failCount(rs.getByte("fail_count"));
            return (T) builder.build();
        }
    }
}
