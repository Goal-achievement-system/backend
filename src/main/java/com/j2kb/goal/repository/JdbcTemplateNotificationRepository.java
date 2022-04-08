package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class JdbcTemplateNotificationRepository implements NotificationRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateNotificationRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Notification insertNotification(Notification notification) {
        String sql = "insert into notification(content,member_email) values(?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"notification_id"});
            preparedStatement.setString(1,notification.getContent());
            preparedStatement.setString(2,notification.getMemberEmail());
            return preparedStatement;
        },keyHolder);
        return selectNotificationById(keyHolder.getKey().longValue());
    }

    @Override
    public List<Notification> selectNotificationsByEmail(String email) {
        String sql = "select * from notification where member_email = ?";
        return jdbcTemplate.query(sql,new NotificationRowMapper<>(),email);
    }

    @Override
    public Notification updateNotification(Notification notification) {
        //String sql = "update notification set content = ?"
        return null;
    }

    private Notification selectNotificationById(long id){
        String sql = "select * from notification where notification_id = ?";
        return jdbcTemplate.query(sql,new NotificationRowMapper<>(),id).get(0);
    }
    private class NotificationRowMapper<T extends Notification> implements RowMapper<T>{

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            Notification.NotificationBuilder builder = Notification.builder();
            builder.notificationId(rs.getLong("notification_id"))
                    .content(rs.getString("content"))
                    .memberEmail(rs.getString("member_email"))
                    .read(rs.getBoolean("read"))
                    .date(rs.getTimestamp("date"));
            return (T)builder.build();
        }
    }
}
