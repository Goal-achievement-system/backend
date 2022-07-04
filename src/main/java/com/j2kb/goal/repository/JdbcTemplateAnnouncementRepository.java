package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcTemplateAnnouncementRepository implements AnnouncementRepository{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateAnnouncementRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public List<Announcement> selectAnnouncements(int page) {
        int start = (page-1)*COUNT_IN_PAGE;
        String sql = "select * from announcement where activation = true limit ?,?";
        return jdbcTemplate.query(sql,new JdbcTemplateAdminRepository.AnnouncementRowMapper<Announcement>(),start,COUNT_IN_PAGE);
    }

    @Override
    public int selectCountsOfAnnouncements() {
        String sql = "select count(*) from announcement";
        return jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        }).get(0);
    }
}
