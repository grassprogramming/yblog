package com.blog.service;

import com.blog.mapper.CommonMapper;
import com.blog.service.CommonDaoService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

/**
 * Created by paul on 2018/6/8.
 */
@Service
public class CommonDaoService{
    @Autowired
    private CommonMapper commonMapper;

    //天坑！！！！DML增删改查语句才会自动回滚，DDL定义数据库结构等擦做语句不会回滚，之前先执行插入，再执行添加字段，就不会回滚了！！！坑！！！
    //参考https://www.cnblogs.com/aguncn/p/5787161.html
    @Transactional
    public void executeSqlWithTransition(List<String> sqlList) throws RuntimeException{
        for (String sql:sqlList){
            System.out.println(sql);
            if (sql.contains("insert")){
                commonMapper.executeTransition_insert(sql);
            }else{
                commonMapper.executeTransition_update(sql);
            }
        }
        //int i=1/0;
    }

    @Transactional
    public void test(String password,String loginid) throws  RuntimeException{
        commonMapper.test(password,loginid);
        int i=1/0;
    }


}
