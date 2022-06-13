package com.j2kb.goal.intercepter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.j2kb.goal.dto.ErrorCode;
import com.j2kb.goal.exception.SpringHandledException;
import com.j2kb.goal.repository.AdminRepository;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminCertInterceptor implements HandlerInterceptor {
    @Autowired
    private AdminRepository adminRepository;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }
        String auth = request.getHeader("Authorization");
        try {
            if (JwtBuilder.isValid(auth)) {
                String email = JwtBuilder.getEmailFromJwt(auth);
                if(adminRepository.isAdmin(email)){
                    return true;
                }else {
                    SpringHandledException exception = new SpringHandledException(HttpStatus.UNAUTHORIZED, ErrorCode.PERMISSION_DENIED,request.getMethod()+" "+request.getRequestURI(),"You are not admin.");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    ObjectMapper mapper = new ObjectMapper();
                    response.getWriter().write(mapper.registerModule(new JavaTimeModule()).writeValueAsString(exception.parseResponseEntity().getBody()));
                    return false;
                }
            } else {
                SpringHandledException exception = new SpringHandledException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN,request.getMethod()+" "+request.getRequestURI(),"token is not invalid");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.registerModule(new JavaTimeModule()).writeValueAsString(exception.parseResponseEntity().getBody()));
                return false;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
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
