package com.blog.listener;

import com.blog.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by paul on 2018/5/29.
 */
//注解方式注册
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    public SessionListener(){
        System.out.println("SessionListener/init:sessionlistenter");
    }
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {

    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("SessionListener/create/sessionid:"+se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("SessionListener/destory/sessionid:"+se.getSession().getId());
    }
}
