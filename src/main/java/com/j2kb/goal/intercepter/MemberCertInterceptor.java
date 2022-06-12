package com.j2kb.goal.intercepter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.MemberRepository;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class MemberCertInterceptor implements HandlerInterceptor {
    @Autowired
    private MemberRepository memberRepository;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }
        String auth = request.getHeader("Authorization");
        try {
            if (JwtBuilder.isValid(auth)) {
                return true;
            } else {
                SpringHandledException exception = new SpringHandledException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN,request.getMethod()+" "+request.getRequestURI(),"token is not invalid");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.registerModule(new JavaTimeModule()).writeValueAsString(exception.parseResponseEntity().getBody()));
                return false;
            }
        }catch (NullPointerException e){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            SpringHandledException exception = new SpringHandledException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND,request.getMethod()+" "+request.getRequestURI(),"token is not found");
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.registerModule(new JavaTimeModule()).writeValueAsString(exception.parseResponseEntity().getBody()));
            return false;
        }finally {
            response.setContentType("application/json");
        }
    }
}
