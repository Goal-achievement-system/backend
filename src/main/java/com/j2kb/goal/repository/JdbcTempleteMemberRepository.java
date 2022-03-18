package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Random;

public class JdbcTempleteMemberRepository implements MemberRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTempleteMemberRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertMember(Member member) {
        String sql = "insert into Member values(?,?,?,?,?,?,?)";
        String salt = makeSalt(member);
        int defaultMoney = 0;
        jdbcTemplate.update(sql,member.getEmail(),member.getPassword(),salt,member.getNickName(),member.getSex().name(),member.getAge(),defaultMoney);
    }
    private String makeSalt(Member member){
        Random random = new Random();
        char[] saltChars = new char[64];
        for(int i=0;i<saltChars.length;i++){
            saltChars[i] = (char) (random.nextInt(75)+48);
        }
        return String.valueOf(saltChars);
    }

    @Override
    public Member selectMemberByMemberEmail(String meberEmail) {
        String sql = "select * from member where email = ?";
        List<Member> result =  jdbcTemplate.query(sql, (rs, rowNum) -> {
            Member.MemberBuilder builder = Member.builder()
                    .email(rs.getString("email"))
                    .password("")
                    .nickName(rs.getString("nickName"))
                    .sex(Member.Sex.valueOf(rs.getString("sex")))
                    .age(rs.getByte("age"))
                    .money(rs.getInt("money"));
            return builder.build();
        },meberEmail);
        if(result.isEmpty()){
            throw new IllegalStateException("No Member with email = "+meberEmail);
        }else{
            return result.get(0);
        }
    }

    @Override
    public void updateMember(Member member) {

    }

    @Override
    public void deleteMember(Member member) {

    }
}
