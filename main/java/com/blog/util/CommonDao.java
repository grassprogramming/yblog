package com.blog.util;

import com.blog.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by paul on 2018/5/10.
 */
//如果使用工具类中的方法报nullpointexception，说明方法类中的dao接口层未注入到spring，需要构造init方法初始化dao接口内部调用
@Component
public class CommonDao {
    @Autowired
    private CommonMapper icommonMapper;

    public static CommonDao commonDao;
    @PostConstruct
    public void init() {
        commonDao = this;
    }
    public List findList(String sql, Class entityClass) throws Exception{
        List<LinkedHashMap<String, Object>> list= commonDao.icommonMapper.findList(sql);
        List returnlist = new ArrayList();
        for(LinkedHashMap<String, Object> map:list){
            Object obj = entityClass.newInstance();
            Field[] fs = entityClass.getDeclaredFields();
            for(Field f :fs){
                f.setAccessible(true);
                f.set(obj,map.get(f.getName()));
            }
            returnlist.add(obj);
        }
        return returnlist;
    }
    public Object FindEntity(String sql, Class entityClass) throws Exception{
        List returnlist = findList(sql,entityClass);
        if (returnlist.size()>0){
            return returnlist.get(0);
        }else{
            return null;
        }
    }
}
