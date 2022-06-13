package com.j2kb.goal.repository;

import com.j2kb.goal.dto.Member;
import com.j2kb.goal.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
@Repository
public class JdbcTemplateMemberRepository implements MemberRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateMemberRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertMember(Member member) {
        String sql = "insert into member values(?,?,?,?,?,?,?)";
        String password = member.getPassword();
        Map<String,String> passwordAndSalt = passwordHashing(password);
        password = passwordAndSalt.get("password");
        String salt = passwordAndSalt.get("salt");
        int defaultMoney = 0;
        jdbcTemplate.update(sql,member.getEmail(),password,salt,member.getNickName(),member.getSex().name(),member.getAge(),defaultMoney);
    }
    private Map<String,String> passwordHashing(String password){
        String salt = makeSalt();
        Map<String,String> map = new HashMap<>();
        map.put("password",SHA256.encrypt(password+salt));
        map.put("salt",salt);
        return map;
    }
    private String makeSalt(){
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
        return result.get(0);
    }

    @Override
    public void minusMoney(Member member, int money) {
        String sql = "update member set money = money - ? where email = ?";
        jdbcTemplate.update(sql,money,member.getEmail());
    }

    @Override
    public void plusMoney(Member member, int money) {
        String sql = "update member set money = money + ? where email = ?";
        jdbcTemplate.update(sql,money,member.getEmail());
    }

    @Override
    public void updateMember(Member member) {
        String password = member.getPassword();
        Map<String,String> passwordAndSalt = passwordHashing(password);
        password = passwordAndSalt.get("password");
        String salt = passwordAndSalt.get("salt");
        String sql = "update member set password = ? , salt = ? , nickname = ? , sex = ? , age = ? where email = ?";
        jdbcTemplate.update(sql,password,salt,member.getNickName(),member.getSex().name(),member.getAge(),member.getEmail());
    }

    @Override
    public void deleteMember(Member member) { // 삭제는 로그인 불가 작업
        String sql = "update member set password = '' and salt = '' and nickname = '' and sex = '' and age = 0 and money = 0 where email = ?";
        jdbcTemplate.update(sql,member.getEmail());
    }

    @Override
    public boolean login(Member member) {
        String sql = "select * from member where email = ? and SHA2(concat(?,salt),256) = password";
        String password = member.getPassword();
        List<Member> results =  jdbcTemplate.query(sql, (rs, rowNum) -> {
            Member.MemberBuilder builder = Member.builder()
                    .email(rs.getString("email"))
                    .password("")
                    .nickName(rs.getString("nickName"))
                    .sex(Member.Sex.valueOf(rs.getString("sex")))
                    .age(rs.getByte("age"))
                    .money(rs.getInt("money"));
            return builder.build();
        },member.getEmail(),password);
        if(results.isEmpty()){
            return false;
        }else {
            Member result = results.get(0);
            if(result.getEmail().equals(member.getEmail())){
                return true;
            }else {
                return false;
            }
        }
    }
}
