package com.blog.controller;

import com.blog.entity.Sys_Config;
import com.blog.util.CommonDao;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

/**
 * Created by paul on 2018/5/30.
 */
@RestController
public class ConfigController {
    private CommonDao commonDao = new CommonDao();

    @RequestMapping("/config/configlist")
    @ResponseBody
    public String home() throws  Exception{
        /*Sys_Config config = new Sys_Config();
        config.setConfigtext("SystemHomeAddress");
        config.setConfigvalue("http://localhost:9090/blog");
        config.setConifgintroduction("系统地址");
        config.setOrdernum(1);
        commonDao.insert(config);*/
        Sys_Config config = (Sys_Config)commonDao.FindEntity(MessageFormat.format("select * from sys_config where rowguid={0}",
                "'48de573d-2f46-40b8-b9f1-4771022af4a0'"),Sys_Config.class);
        config.setConifgintroduction("系统地址说明");
        commonDao.update(config,"rowguid");
        return "Hello World!";
    }
}
