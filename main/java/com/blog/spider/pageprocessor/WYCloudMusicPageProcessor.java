package com.blog.spider.pageprocessor;

import com.blog.gxh.entity.MusicComment;
import com.blog.gxh.entity.MusicSong;
import com.blog.spider.entity.AuthorInfo;
import com.blog.util.CommonDao;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
        //模拟手机
        ChromeOptions options = new ChromeOptions();
        //设置user agent为iphone5
        options.addArguments("--user-agent=Apple Iphone 7");

        WebDriver driver = new ChromeDriver(options);
        //最大化窗口
        driver.manage().window().maximize();
        //设置隐性等待时间
        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);

        // get()打开一个站点
        driver.get("https://wenku.baidu.com/view/6f569bbc70fe910ef12d2af90242a8956becaa2c.html");
        try {
            Thread.sleep(5000);
        }catch (Exception e){
            System.out.println("thread error:");
            e.printStackTrace();
        }
      /*  //滚动到最下面
        Actions action = new Actions(driver);
        action.moveToElement(driver.findElement(By.cssSelector("div[class='footer']"))).perform();
        try {
            Thread.sleep(3000);
        }catch (Exception e){
            System.out.println("thread error:");
            e.printStackTrace();
        }
        //获取更多按钮并点击
        WebDriverWait wait = new WebDriverWait(driver, 20);
        //com.google.guava版本问题引起的传入函数条件不满足泛型
        WebElement morebtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[class^='moreBtn']")));
        morebtn.click();*/

        try {
            Thread.sleep(3000);
        }catch (Exception e){
            System.out.println("thread error:");
            e.printStackTrace();
        }

        try {
            driver.findElement(By.className("foldpagewg-text-con")).click();
            Thread.sleep(2000);
        }catch (Exception e){

        }

        StringBuilder doc = new StringBuilder();
        List<WebElement> doccontentlist = driver.findElements(By.cssSelector("div[class^='rtcspage']"));
        for(WebElement doccontent:doccontentlist){
            List<WebElement> plist = doccontent.findElements(By.cssSelector("p[class^='rtcscls']"));
            for(WebElement p:plist){
                doc.append(p.getText());
                doc.append("\n");
            }
        }
        System.out.println(doc);
      /*  driver.close();
        driver.quit();*/

    }

    @Override
    public Site getSite() {
        return site;
    }
}
