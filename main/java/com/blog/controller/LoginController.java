package com.blog.controller;


import com.blog.entity.Frame_User;
import com.blog.service.CommonDaoService;
import com.blog.spider.pageprocessor.WYCloudMusicPageProcessor;
import com.blog.util.CommonDao;
import com.blog.util.Encrpt;
import com.blog.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import us.codecraft.webmagic.Spider;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2018/5/10.
 */
@RestController
public class LoginController {

    public CommonDao commonDao = new CommonDao();
    @Autowired
    public CommonDaoService commonDaoService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    DefaultKaptcha defaultKaptcha;

    @RequestMapping("/")
    @ResponseBody
    public String home() {
       /* try {
            //commonDaoService.test("723904","cnm");
            WYCloudMusicPageProcessor test = new WYCloudMusicPageProcessor();
            Spider.create(test)
                    .addUrl("https://music.163.com/#/song?id=308299")
//              .addPipeline(netEaseMusicPipeline)
                    .run();
        }catch (Exception e){

        }*/

        /*Frame_User user = new Frame_User();
        user.setDisplayname("test1");
        user.setLoginid("login1");
        user.setUserguid("aaaaaa");
        user.setPassword("asdasdasda");

        Frame_User user1 = new Frame_User();
        user1.setDisplayname("test2");
        user1.setLoginid("login2");
        user1.setUserguid("bbbbbbbb");
        user1.setPassword("sssssss");

        List<Frame_User> frameuserlist = new ArrayList<Frame_User>();
        frameuserlist.add(user);
        frameuserlist.add(user1);
        try {
            commonDao.insertBatch(frameuserlist);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        return "Hello World!";
    }


    @RequestMapping("/login/loginin")
    @ResponseBody
    public Map<Object,Object> getAllUser(String loginid,String auth,HttpServletRequest request) throws Exception{
        String captchaId = (String) request.getSession().getAttribute("vrifyCode");
        String parameter = request.getParameter("imagecode");
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
                if (!captchaId.equals(parameter)){
                    map.put("status","0");
                    map.put("message","验证码不正确！");
                }else{
                    map.put("status","1");
                    map.put("message","用户存在！");
                    sessionmap.put("loginid",loginid);
                    sessionmap.put("username",user.getDisplayname());
                    //以json方式存入
                    redisUtil.redisTemplateSet(request.getSession().getId(),sessionmap);
                    //以list方式存入,具体内容以键值对形式存储，结构比较清晰
                    //redisUtil.redisTemplateSetForList(request.getSession().getId(),sessionmap);
                }

            }else{
                map.put("status","0");
                map.put("message","密码不正确！");
            }
        }
        return map;
    }

    @RequestMapping("/login/getidentificodeing")
    public void getidentificodeing(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws Exception{
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            httpServletRequest.getSession().setAttribute("vrifyCode", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }



    }
