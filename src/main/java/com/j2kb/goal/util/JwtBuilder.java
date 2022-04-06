package com.j2kb.goal.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

public class JwtBuilder {
    public static String build(String email){
        String header = "\"typ\": \"JWT\"," +
                " \"alg\": \"HS256\"";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String payload = "{\"exp\": "+LocalDateTime.now().plusDays(1).format(formatter)+"," +
                "\"email\": \""+email+"\"}";
        header = Base64.getEncoder().encodeToString(header.getBytes());
        payload = Base64.getEncoder().encodeToString(payload.getBytes());
        String sign = header+"."+payload;
        String jwt = sign;
        sign = AES256.encrypt(jwt);
        jwt = jwt+"."+sign;
        return jwt;
    }
    public static String getEmailFromJwt(String jwt){
        if(!isValid(jwt)){
            return "";
        }
        String[] jwtArray = jwt.split(".");
        String header = jwtArray[0];
        String payload = jwtArray[1];
        String sign = jwtArray[2];
        return new String(Base64.getDecoder().decode(payload));
    }
    public static boolean isValid(String jwt){
        String[] jwtArray = jwt.split("\\.");
        String header = jwtArray[0];
        String payload = jwtArray[1];
        String sign = jwtArray[2];
        String headerPayload = header+"."+payload;
        sign = AES256.decrypt(sign);
        try {
            if (headerPayload.contentEquals(sign)) {
                payload = new String(Base64.getDecoder().decode(payload));
                Map<String, String> jwtMap = new ObjectMapper().readValue(payload, new TypeReference<Map<String, String>>() {
                });
                String exp = jwtMap.get("exp");
                LocalDateTime expLocalDateTime = LocalDateTime.parse(exp, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(expLocalDateTime)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
