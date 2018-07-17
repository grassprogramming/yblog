package com.blog.controller;


import com.blog.entity.Frame_User;
import com.blog.service.CommonDaoService;
import com.blog.util.*;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.imageio.ImageIO;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by paul on 2018/5/10.
 */
@RestController
public class LoginController {

    public CommonDao commonDao = new CommonDao();
    public MailUtil mailUtil = new MailUtil();
    @Autowired
    public CommonDaoService commonDaoService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private FreeMarkerConfigurer configurer;


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

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        String decrptdata = "";
        String data = "123aabb哈哈哈哈哈_asad1";
        try {
            String pubkpath = ResourceUtils.getURL("src/main/resources/static/lic/").getPath()+"yblog.crt";
            String prikeypath = "C:/Users/paul/Desktop/RSA加密（JAVA）/yblog.keystore";
            String encrptdata  = Encrpt.EncriptWRSA_Pri(data,prikeypath);
            decrptdata = Encrpt.DecriptWithRSA_Pub(encrptdata,pubkpath);
        }catch (Exception e){
            e.printStackTrace();
        }
        return decrptdata;
    }

    @RequestMapping("/mailtest")
    @ResponseBody
    public String mailtest() {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("name", "张久久");
            model.put("workID", "12030524");
            model.put("contractTerm", "3");
            model.put("beginContract", Calendar.getInstance().getTime());
            model.put("endContract", Calendar.getInstance().getTime());
            model.put("departmentName", "研发一部");
            model.put("posName", "研发工程师");
            mailUtil.SenHtmlMail_FMTemplate("yanpeng19940119@gmail.com","YBLOG邮件","mail_hello.ftl",model);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "ok";
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

    @RequestMapping(value="/login/getmachinecode",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> getmachinecode(@RequestBody Map<String,Object> reqMap){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String machineCode =LicenseAuth.getMachineCode();
            map.put("machinecode", machineCode);
            map.put("executestatus","1");
        }catch (Exception e){
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/login/getlicense",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> getLicense(@RequestBody Map<String,Object> reqMap,HttpServletRequest request){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String LicensePath=MessageFormat.format("D:/{0}.lic",sdf.format(new Date())+UUID.randomUUID().toString().replace("-","").substring(0,8));
            String prikeypath = "C:/Users/paul/Desktop/RSA加密（JAVA）/yblog.keystore";
            String machinecode =reqMap.get("machinecode").toString();
            String islimit =reqMap.get("islimit").toString();
            String liclimit =reqMap.get("liclimit").toString();
            LicenseAuth.getLicense(islimit,liclimit,machinecode,LicensePath,prikeypath);
            String downloadurl = CommonUtil.GetProjectUrl(request)+"attach/tempfiledownload?filepath="+ URLEncoder.encode(LicensePath);
            map.put("downloadurl",downloadurl);
            map.put("executestatus","1");
        }catch (Exception e){
            e.printStackTrace();
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/login/licenseauth",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> licenseauth(MultipartHttpServletRequest multiReq){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            String savePath = ResourceUtils.getURL("src/main/resources/static/lic").getPath();
            MultipartFile file = multiReq.getFile("file");
            String filename = file.getOriginalFilename();
            File uploadfile = new File(savePath + "\\" + filename);
            if (!uploadfile.exists()){
                //获取item中的上传文件的输入流
                InputStream in = file.getInputStream();
                //创建一个文件输出流
                FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
                //创建一个缓冲区
                byte buffer[] = new byte[1024];
                //判断输入流中的数据是否已经读完的标识
                int len = 0;
                //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                while((len=in.read(buffer))>0){
                    //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                    out.write(buffer, 0, len);
                }
                //关闭输入流
                in.close();
                //关闭输出流
                out.close();
            }
            map.put("executestatus","1");

        }catch (Exception e){
            e.printStackTrace();
            map.put("executestatus","0");
        }

        return map;
    }

    @RequestMapping(value="/login/licensevalidate",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> licensevalidate(){
        Map<Object,Object>  map = new HashMap<Object,Object>();
        try {
            if (LicenseAuth.authLicense()){
                map.put("executestatus","1");
            }else{
                map.put("executestatus","0");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

}
