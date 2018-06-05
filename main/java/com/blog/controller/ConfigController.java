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
        sql +=" order by ordernum desc";
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

    @RequestMapping(value="/config/configadd",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> configadd(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String configtext = reqMap.get("configtext").toString();
            String configvalue = reqMap.get("configvalue").toString();
            String conifgintroduction = reqMap.get("conifgintroduction").toString();
            int ordernum = Integer.parseInt(reqMap.get("ordernum").toString());
            Sys_Config sysconfig = new Sys_Config();
            sysconfig.setConfigtext(configtext);
            sysconfig.setConfigvalue(configvalue);
            sysconfig.setConifgintroduction(conifgintroduction);
            sysconfig.setOrdernum(ordernum);
            commonDao.insert(sysconfig);
            map.put("executestatus","1");
        }catch (Exception e){
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/config/configdelete",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> configdelete(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            String sql = MessageFormat.format("delete from {0} where rowguid={1}","Sys_Config","'"+rowguid+"'");
            icommonMapper.executeSql(sql);
            map.put("executestatus","1");
        }catch (Exception e){
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/config/findone",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> configfindone(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String rowguid = reqMap.get("rowguid").toString();
            Sys_Config sys_config = (Sys_Config)commonDao.FindEntityWithRowGuid("sys_config",rowguid,Sys_Config.class);
            map.put("data",sys_config);
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/config/configsave",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> configsave(@RequestBody Sys_Config sys_config){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            commonDao.update(sys_config,"rowguid");
            map.put("executestatus","1");
        }catch (Exception e){
            System.out.println(e.toString());
            map.put("executestatus","0");
        }

        return map;
    }
}
