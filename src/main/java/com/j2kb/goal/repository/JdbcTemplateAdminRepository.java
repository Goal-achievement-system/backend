package com.j2kb.goal.repository;

import com.j2kb.goal.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
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
        String sql = "select * from admin_list where email = ? and SHA2(concat(?,salt),256) = password";
        String password = admin.getPassword();
        List<Admin> results = jdbcTemplate.query(sql,(rs, rowNum) -> {
            Admin selectedAdmin = new Admin(rs.getString("email"),"");
            return selectedAdmin;
        },admin.getEmail(),admin.getPassword());
        if(results.isEmpty()){
            return false;
        }else{
            Admin result = results.get(0);
            if(result.getEmail().equals(admin.getEmail())){
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public boolean isAdmin(String email) {
        String sql = "select email from admin_list where email = ?";
        List<String> results = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        },email);
        if(results.isEmpty()){
            return false;
        }else {
            System.out.println(email);
            System.out.println(results.get(0));
            return results.get(0).contentEquals(email);
        }
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
    public long insertAnnouncement(Announcement announcement) {
        String sql = "insert into announcement(title,description,content,banner,activation) values(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"announcement_id"});
            ps.setString(1, announcement.getTitle());
            ps.setString(2,announcement.getDescription());
            ps.setString(3,announcement.getImage());
            ps.setString(4,announcement.getBannerImage());
            ps.setBoolean(5,announcement.isActivation());
            return ps;
        },keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public int selectHoldGoalAndCertsMaxPage() {
        String sql = "select count(*) from goal INNER JOIN certification ON goal.verification_result = 'hold' and goal.goal_id = certification.goal_id";
        int maxPage = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt(1)).get(0);
        if(maxPage % GOAL_COUNT!=0){
            maxPage = maxPage / GOAL_COUNT +1;
        }else {
            maxPage = maxPage / GOAL_COUNT;
        }
        return maxPage;
    }

    static class AnnouncementRowMapper<T extends Announcement> implements RowMapper<T>{
        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            Announcement.AnnouncementBuilder builder = Announcement.builder();
            return (T)builder.announcementId(rs.getLong("announcement_id"))
                    .title(rs.getString("title"))
                    .description("description")
                    .image("content")
                    .date(rs.getTimestamp("date"))
                    .bannerImage(rs.getString("banner"))
                    .activation(rs.getBoolean("activation")).build();
        }
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
            Certification.CertificationBuilder builder = Certification.builder();
            builder.certId(rs.getLong("cert_id"))
                    .content(rs.getString("cert_content"))
                    .image("image")
                    .goalId(rs.getLong("goal_id"))
                    .verificationResult(rs.getString("verification_result"));
            Certification certification = builder.build();
            return (T) new GoalAndCert(goal,certification);
        }
    }
}
