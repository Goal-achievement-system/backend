package com.j2kb.goal.config;

import com.j2kb.goal.intercepter.MemberCertInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private DataSource dataSource;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberCertInterceptor())
                .addPathPatterns("/**") // 해당 경로에 접근하기 전에 인터셉터가 가로챈다.
                .excludePathPatterns("/api/members")
                .excludePathPatterns("/api/members/login");// 해당 경로는 인터셉터가 가로채지 않는다.
    }
    @Bean
    public PlatformTransactionManager transactionManager(){
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource);
        return tm;
    }
}
