package com.blog.interceptor;

import com.blog.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul on 2018/5/29.
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //TODO 请求拦截处理逻辑
        boolean islogin = false;
        HttpSession session = httpServletRequest.getSession(true);
        System.out.println("SessionInterceptor/create/sessionid:"+session.getId());

        Map<String,String> sessionmap = null;
        try {
            sessionmap = (Map<String,String>)redisUtil.redisTemplateGet(session.getId());
            if (null!=sessionmap){
                islogin = true;
            }

        }catch (Exception e){

        }
        if (!islogin){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/pages/login/loginmain.html");
        }
        return islogin;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //System.out.println("TestInterceptor: postHandle");


    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //System.out.println("TestInterceptor: afterCompletion");


    }
}
