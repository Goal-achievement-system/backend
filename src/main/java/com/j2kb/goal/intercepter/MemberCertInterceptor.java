package com.j2kb.goal.intercepter;


import com.j2kb.goal.repository.MemberRepository;
import com.j2kb.goal.util.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
        if(JwtBuilder.isValid(auth)){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) throws Exception {
    }

}
