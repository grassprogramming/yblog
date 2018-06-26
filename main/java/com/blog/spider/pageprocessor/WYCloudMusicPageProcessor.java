package com.blog.spider.pageprocessor;

import com.blog.gxh.entity.MusicComment;
import com.blog.gxh.entity.MusicSong;
import com.blog.spider.entity.AuthorInfo;
import com.blog.util.CommonDao;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by paul on 2018/6/19.
 */
@Component
public class WYCloudMusicPageProcessor implements PageProcessor {
    public CommonDao commonDao = new CommonDao();
    private Site site = Site.me()
            .setSleepTime(1000)
            .setRetryTimes(30)
            .setCharset("utf-8")
            .setTimeOut(30000)
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        /*DesiredCapabilities dcaps = new DesiredCapabilities();
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
        driver.get("https://music.163.com/#/song?id=308299");

        //切换到内嵌iframe中
        driver.switchTo().frame("g_iframe");

        //获取分页栏
        WebElement pagecontent = driver.findElement(By.cssSelector("div[class$='u-page']"));
        List<WebElement> pagecountlist = pagecontent.findElements(By.tagName("a"));
        String allpagecount = pagecountlist.get(pagecountlist.size()-2).getText();
        System.out.println("pagecount:"+allpagecount);

        //插入歌曲信息
        MusicSong musicSong = new MusicSong();
        musicSong.setSongName("喜帖街");
        musicSong.setSongAddress("https://music.163.com/#/song?id=308299");
        commonDao.insert(musicSong);

        for (int i=1;i<=Integer.parseInt(allpagecount);i++){
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

                try {
                    //插入评论
                    MusicComment musicComment = new MusicComment();
                    musicComment.setNickName(nickname);
                    musicComment.setCommentContent(commentcontent);
                    musicComment.setCommentDate(commentdate);
                    musicComment.setSongGuid(musicSong.getRowguid());
                    commonDao.insert(musicComment);
                }catch (Exception e){

                }
            }
            //点击下一下
            pagecountlist.get(pagecountlist.size()-1).click();
        }


        driver.close();
        driver.quit();*/


        //单独使用chromedriver
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        //初始化一个chrome浏览器实例，实例名称叫driver
        WebDriver driver = new ChromeDriver();
        //最大化窗口
        driver.manage().window().maximize();
        //设置隐性等待时间
        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);

        // get()打开一个站点
        driver.get("https://music.163.com/#/search/m/?s=%E5%96%9C%E5%B8%96%E8%A1%97&type=1");
        try {
            Thread.sleep(3000);
        }catch (Exception e){
            System.out.println("thread error:");
            e.printStackTrace();
        }
        driver.switchTo().frame("g_iframe");
        driver.findElement(By.cssSelector("a[data-type='100']")).click();

        List<WebElement> authorinfolist = driver.findElement(By.cssSelector("ul[class^='m-cvrlst']")).findElements(By.cssSelector("div[class^='u-cover']"));
        List<AuthorInfo> authorlist = new ArrayList<AuthorInfo>();
        for(WebElement author:authorinfolist){
            AuthorInfo authorInfo = new AuthorInfo();
            String imageurl = author.findElement(By.tagName("img")).getAttribute("src");
            String authorname = author.findElement(By.tagName("span")).getAttribute("title");
            System.out.println(authorname+":"+imageurl);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }
}
