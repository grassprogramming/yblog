package com.blog.config;


import com.blog.interceptor.SessionInterceptor;
import com.blog.interceptor.WebMachineRegistInterceptor;
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
    @Autowired
    private WebMachineRegistInterceptor webMachineRegistInterceptor;
    final String[] notLoginInterceptPaths = {"/common/**","/plugins/**","/webjars/**","/pages/login/**","/error","/login/**"};
    final String[] notRegistInterceptPath = {"/common/**","/plugins/**","/webjars/**","/login/**","/attach/**","/error","/error","/pages/login/licenseauth.html","/pages/login/licenseregist.html"};
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webMachineRegistInterceptor).addPathPatterns("/**").excludePathPatterns(notRegistInterceptPath);
        //registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns(notLoginInterceptPaths);
    }

}
