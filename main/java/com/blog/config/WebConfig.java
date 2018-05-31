package com.blog.config;


import com.blog.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by paul on 2018/5/29.
 */
@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;
    final String[] notLoginInterceptPaths = {"/common/**","/webjars/**","/pages/login/**","/error","/login/**"};
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns(notLoginInterceptPaths);
    }

}
