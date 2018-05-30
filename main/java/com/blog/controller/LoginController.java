package com.blog.controller;


import com.blog.entity.Frame_User;
import com.blog.util.CommonDao;
import com.blog.util.Encrpt;
import com.blog.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2018/5/10.
 */
@RestController
public class LoginController {

    private CommonDao commonDao = new CommonDao();
    @Autowired
    private RedisUtil redisUtil;
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }


    @RequestMapping("/login/loginin")
    @ResponseBody
    public Map<Object,Object> getAllUser(String loginid,String auth,HttpServletRequest request) throws Exception{
        Map<Object,Object> sessionmap = new HashMap<>();
        Map<Object,Object>  map = new HashMap<Object,Object>();
        String sql = MessageFormat.format("select * from frame_user where loginid={0}",new Object[]{"'"+loginid+"'"});
        System.out.println(sql);
        Frame_User user = (Frame_User)commonDao.FindEntity(sql, Frame_User.class);
        if (null==user){
            map.put("status","0");
            map.put("message","用户不存在！");
        }else{
            String caculateauth = Encrpt.GetMD5Code(user.getLoginid()+user.getPassword());
            if (auth.equals(caculateauth)){
                map.put("status","1");
                map.put("message","用户存在！");
                sessionmap.put("loginid",loginid);
                sessionmap.put("username",user.getDisplayname());
                //以json方式存入
                redisUtil.redisTemplateSet(request.getSession().getId(),sessionmap);
                //以list方式存入,具体内容以键值对形式存储，结构比较清晰
                //redisUtil.redisTemplateSetForList(request.getSession().getId(),sessionmap);
            }else{
                map.put("status","0");
                map.put("message","密码不正确！");
            }
        }
        return map;
    }


}
