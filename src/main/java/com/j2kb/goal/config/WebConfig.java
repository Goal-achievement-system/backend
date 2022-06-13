package com.j2kb.goal.config;

import com.j2kb.goal.intercepter.AdminCertInterceptor;
import com.j2kb.goal.intercepter.MemberCertInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private DataSource dataSource;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberCertInterceptor())
                .addPathPatterns("/api/**","/api/members/myinfo") // 해당 경로에 접근하기 전에 인터셉터가 가로챈다.
                .excludePathPatterns("/api/members","/api/members/login","/api/admin","/api/statistics/total","/api/admin/**","/api/members/check/email/*");// 해당 경로는 인터셉터가 가로채지 않는다.
        registry.addInterceptor(adminCertInterceptor())
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource);
        return tm;
    }
    @Bean
    public AdminCertInterceptor adminCertInterceptor(){
        return new AdminCertInterceptor();
    }
}
