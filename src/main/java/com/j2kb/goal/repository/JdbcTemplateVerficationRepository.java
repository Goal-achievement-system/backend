package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
@Repository
public class JdbcTemplateVerficationRepository implements VerificationRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateVerficationRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertVerification(Verification verification) {
        String sql = "insert into verification_list(member_email,cert_id) values(?,?)";
        jdbcTemplate.update(sql,verification.getMemberEmail(),verification.getCertId());
    }
}
