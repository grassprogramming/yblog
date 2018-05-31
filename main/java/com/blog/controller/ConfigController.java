package com.blog.controller;

import com.blog.entity.Sys_Config;
import com.blog.mapper.CommonMapper;
import com.blog.util.CommonDao;
import com.blog.util.PageBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2018/5/30.
 */
@RestController
public class ConfigController {
    private CommonDao commonDao = new CommonDao();
    @Autowired
    private CommonMapper icommonMapper;

    @RequestMapping(value="/config/configlist",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> configlist(@RequestBody Map<String,Object> reqMap) throws  Exception{
        //新增测试
        /*Sys_Config config = new Sys_Config();
        config.setConfigtext("SystemHomeAddress");
        config.setConfigvalue("http://localhost:9090/blog");
        config.setConifgintroduction("系统地址");
        config.setOrdernum(1);
        commonDao.insert(config);*/
        //更新测试
       /* Sys_Config config = (Sys_Config)commonDao.FindEntity(MessageFormat.format("select * from sys_config where rowguid={0}",
                "'48de573d-2f46-40b8-b9f1-4771022af4a0'"),Sys_Config.class);
        config.setConifgintroduction("系统地址说明");
        commonDao.update(config,"rowguid");*/
        int pageNum =Integer.parseInt(reqMap.get("pageNum").toString());
        int pageSize =Integer.parseInt(reqMap.get("pageSize").toString());
        String searchConfigName = reqMap.get("searchConfigName").toString();
        int count = icommonMapper.queryInt("select count(1) from sys_config");
        PageHelper.startPage(pageNum, pageSize);
        //数据库操作要紧随分页事件后，有且只有后面的一个个sql会被分页
        String sql = "select * from sys_config where 1=1 ";
        if (!StringUtil.isEmpty(searchConfigName)){
            sql += MessageFormat.format(" and configtext like {0}","'%"+searchConfigName+"%'");
        }
        List<Sys_Config> list = commonDao.findList(sql,Sys_Config.class);
        PageBean<Sys_Config> pageData = new PageBean<Sys_Config>(pageNum, pageSize,count);
        pageData.setItems(list);
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",list);
        map.put("currentPage",pageNum);
        map.put("pagesize",pageSize);
        map.put("totalnum",pageData.getTotalNum());
        return map;
    }
}
