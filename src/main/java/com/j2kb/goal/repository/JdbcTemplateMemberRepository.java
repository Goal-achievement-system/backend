package com.j2kb.goal.repository;

import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.dto.Member;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        String sql = "update member set nickname = ? , sex = ? , age = ? where email = ? and SHA2(concat(?,salt),256) = password";
        jdbcTemplate.update(sql,member.getNickName(),member.getSex().name(),member.getAge(),member.getEmail(),member.getPassword());
    }

    @Override
    public void updateMemberPassword(Map<String, String> passwords, String memberEmail) {
        String nowPassword = passwords.get("password");
        String newPassword = passwords.get("newPassword");
        Map<String,String> passwordAndSalt = passwordHashing(newPassword);
        String salt = passwordAndSalt.get("salt");
        newPassword = passwordAndSalt.get("password");
        String sql = "update member set password = ?, salt = ? where email = ? and SHA2(concat(?,salt),256) = password";
        jdbcTemplate.update(sql,newPassword,salt,memberEmail,nowPassword);
    }

    @Override
    public void deleteMember(Member member) { // ????????? ????????? ?????? ??????
        String sql = "update member set password = '',salt = '', nickname = '',age = 0 where email = ? and money = 0 and SHA2(concat(?,salt),256) = password";
        if(selectMemberByMemberEmail(member.getEmail()).getMoney()!=0){
            throw new SpringHandledException(HttpStatus.CONFLICT, ErrorCode.LACK_OF_MONEY, "DELETE api/members/myinfo/withdrawal","can not delete member where money is not 0");
        }
        if(jdbcTemplate.update(sql,member.getEmail(),member.getPassword())!=1){
            throw new SpringHandledException(HttpStatus.CONFLICT, ErrorCode.LACK_OF_MONEY, "DELETE api/members/myinfo/withdrawal","can not delete member where money is not 0");
        }
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
