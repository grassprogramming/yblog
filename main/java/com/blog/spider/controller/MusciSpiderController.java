package com.blog.spider.controller;

import com.blog.gxh.entity.MusicComment;
import com.blog.gxh.entity.MusicSong;
import com.blog.spider.entity.WYCloudMusic;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2018/6/25.
 */
@RestController
public class MusciSpiderController {
    @RequestMapping(value="/spider/music/searchwithname",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> searchwithname(@RequestBody Map<String,Object> reqMap) throws  Exception{
        String musicname = reqMap.get("musciname").toString();
        String loadurl = MessageFormat.format("https://music.163.com/#/search/m/?s={0}&type=1", URLEncoder.encode(musicname));
        //抓取网页
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        try {
            //驱动支持
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    ResourceUtils.getURL("src/main/resources/static/plugins/phantomjs/").getPath()+"phantomjs.exe");
        }catch (Exception e){
            System.out.println("phantomjs error");
            e.printStackTrace();
        }
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        driver.get(loadurl);
        //切换到内嵌iframe中
        driver.switchTo().frame("g_iframe");
        List<WebElement> songlist = driver.findElement(By.className("srchsongst")).findElements(By.cssSelector("div[class^='item']"));
        List<WYCloudMusic> datalist = new ArrayList<WYCloudMusic>();
        for(WebElement song :songlist){
            WYCloudMusic music = new WYCloudMusic();
            String url = URLEncoder.encode(song.findElement(By.cssSelector("div[class$='w0']")).findElement(By.tagName("a")).getAttribute("href"));
            String musciname = song.findElement(By.cssSelector("div[class$='w0']")).findElement(By.tagName("b")).getAttribute("title");
            String author = song.findElement(By.cssSelector("div[class$='w1']")).findElement(By.tagName("a")).getText();
            String album = song.findElement(By.cssSelector("div[class$='w2']")).findElement(By.tagName("a")).getText();
            String time = song.findElement(By.cssSelector("div:nth-child(6)")).getText();
            music.setMusicname(musciname);
            music.setAuthor(author);
            music.setAlbum(album);
            music.setTime(time);
            music.setUrl(url);
            datalist.add(music);
        }
        driver.close();
        driver.quit();
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",datalist);
        return map;
    }

    @RequestMapping(value="/spider/music/searchcomment",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> searchcomment(@RequestBody Map<String,Object> reqMap) throws  Exception{
        String url = reqMap.get("url").toString();
        //抓取网页
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        try {
            //驱动支持
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    ResourceUtils.getURL("src/main/resources/static/plugins/phantomjs/").getPath()+"phantomjs.exe");
        }catch (Exception e){
            System.out.println("phantomjs error");
            e.printStackTrace();
        }
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        driver.get(url);
        //切换到内嵌iframe中
        driver.switchTo().frame("g_iframe");

        List<MusicComment> datalist = new ArrayList<MusicComment>();
        for (int i=1;i<=5;i++){
            System.out.println("===================================================");
            System.out.println("第"+i+"页");
            List<WebElement> commentlist =  driver.findElement(By.className("m-cmmt")).findElements(By.className("itm"));
            for(WebElement comment :commentlist){
                WebElement content = comment.findElement(By.className("cntwrap"));
                String nickname = content.findElement(By.tagName("a")).getText();
                String commentcontent =content.findElement(By.cssSelector("div[class^='cnt']")).getText().replace(nickname+"：","");
                String commentdate = content.findElement(By.cssSelector("div[class^='time']")).getText();
                System.out.println("昵称："+nickname);
                System.out.println("评论内容："+commentcontent);
                System.out.println("评论时间："+commentdate);
                MusicComment musicComment = new MusicComment();
                musicComment.setNickName(nickname);
                musicComment.setCommentContent(commentcontent);
                musicComment.setCommentDate(commentdate);
                datalist.add(musicComment);
            }
        }

        driver.close();
        driver.quit();
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",datalist);
        return map;
    }
}
