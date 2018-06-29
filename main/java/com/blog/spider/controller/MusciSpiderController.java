package com.blog.spider.controller;

import com.blog.gxh.entity.MusicComment;
import com.blog.gxh.entity.MusicSong;
import com.blog.spider.entity.AlbumInfo;
import com.blog.spider.entity.AuthorInfo;
import com.blog.spider.entity.SongList;
import com.blog.spider.entity.WYCloudMusic;
import com.blog.util.CommonUtil;
import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.*;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        String DRIVER_PATH = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver",DRIVER_PATH);
        ChromeOptions opt = new ChromeOptions();
        //使用谷歌的无头模式 PhantomJS后续高版本不在收到seleunim的支持，且开源暂停开发
        opt.addArguments("headless");
        WebDriver driver = new ChromeDriver(opt);
        driver.get(loadurl);
        //切换到内嵌iframe中
        driver.switchTo().frame("g_iframe");

        //单曲
        List<WebElement> songlist = driver.findElement(By.className("srchsongst")).findElements(By.cssSelector("div[class^='item']"));
        List<WYCloudMusic> datalist = new ArrayList<WYCloudMusic>();
        for(WebElement song :songlist){
            WYCloudMusic music = new WYCloudMusic();
            String songid =  song.findElement(By.cssSelector("div:nth-child(1)")).findElement(By.tagName("a")).getAttribute("data-res-id");
            String url = URLEncoder.encode(song.findElement(By.cssSelector("div[class$='w0']")).findElement(By.tagName("a")).getAttribute("href"));
            String musciname = song.findElement(By.cssSelector("div[class$='w0']")).findElement(By.tagName("b")).getAttribute("title");
            String author = song.findElement(By.cssSelector("div[class$='w1']")).findElement(By.className("text")).getText();
            String album = song.findElement(By.cssSelector("div[class$='w2']")).findElement(By.tagName("a")).getText();
            String time = song.findElement(By.cssSelector("div:nth-child(6)")).getText();
            music.setSongid(songid);
            music.setMusicname(musciname);
            music.setAuthor(author);
            music.setAlbum(album);
            music.setTime(time);
            music.setUrl(url);
            datalist.add(music);
        }



        //歌手
        driver.findElement(By.cssSelector("a[data-type='100']")).click();
        //等待页面数据加载完成
        WebDriverWait wait = new WebDriverWait(driver, 20);
        //com.google.guava版本问题引起的传入函数条件不满足泛型
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul[class^='m-cvrlst']")));
        List<WebElement> authorinfolist = driver.findElement(By.cssSelector("ul[class^='m-cvrlst']")).findElements(By.cssSelector("div[class^='u-cover']"));
        List<AuthorInfo> authorlist = new ArrayList<AuthorInfo>();
        for(WebElement author:authorinfolist){
            AuthorInfo authorInfo = new AuthorInfo();
            String imageurl = author.findElement(By.tagName("img")).getAttribute("src");
            String authorname = author.findElement(By.tagName("span")).getAttribute("title");
            authorInfo.setAutorname(authorname);
            authorInfo.setImageurl(imageurl);
            authorlist.add(authorInfo);
        }

        //专辑
        driver.findElement(By.cssSelector("a[data-type='10']")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul[class^='m-cvrlst']")));
        List<WebElement> albuminfolist = driver.findElement(By.cssSelector("ul[class^='m-cvrlst']")).findElements(By.cssSelector("div[class^='u-cover']"));
        List<AlbumInfo> albumlist = new ArrayList<AlbumInfo>();
        for(WebElement album:albuminfolist){
            AlbumInfo albumInfo = new AlbumInfo();
            String imageurl = album.findElement(By.tagName("img")).getAttribute("src");
            String albumname = album.findElement(By.tagName("span")).getAttribute("title");
            albumInfo.setAlbumname(albumname);
            albumInfo.setImageurl(imageurl);
            albumlist.add(albumInfo);
        }

        //歌单
        driver.findElement(By.cssSelector("a[data-type='1000']")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table[class^='m-table']")));
        List<WebElement> songinfolist = driver.findElement(By.cssSelector("table[class^='m-table']")).findElements(By.cssSelector("tr[class^='h-flag']"));
        List<SongList> songmenulist = new ArrayList<SongList>();
        for (WebElement songlistinfo :songinfolist){
            SongList songList = new SongList();
            String imageurl = songlistinfo.findElement(By.className("w7")).findElement(By.tagName("img")).getAttribute("src");
            String songlistname = songlistinfo.findElement(By.className("ttc")).findElement(By.tagName("a")).getAttribute("title");
            String songlistcount =  songlistinfo.findElement(By.cssSelector("td[class^='w11']")).getText();
            String createauthorname = songlistinfo.findElement(By.className("w4")).findElement(By.tagName("a")).getAttribute("title");
            String collectcount = songlistinfo.findElement(By.cssSelector("td[class^='w6']")).findElement(By.tagName("span")).getText();
            String listencount = songlistinfo.findElement(By.cssSelector("td[class^='last']")).findElement(By.tagName("span")).getText();
            songList.setImageurl(imageurl);
            songList.setSonglistname(songlistname);
            songList.setSonglistcount(songlistcount);
            songList.setCreateauthorname(createauthorname);
            songList.setCollectcount(collectcount);
            songList.setListencount(listencount);
            songmenulist.add(songList);
        }
        driver.close();
        driver.quit();
        Map<Object,Object>  map = new HashMap<Object,Object>();
        map.put("datalist",datalist);
        map.put("authorlist",authorlist);
        map.put("albumlist",albumlist);
        map.put("albumlist",albumlist);
        map.put("songmenulist",songmenulist);
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

    @RequestMapping(value="/spider/music/download",method= RequestMethod.POST)
    @ResponseBody
    public Map<Object,Object> download(@RequestBody Map<String,Object> reqMap,HttpServletRequest request) throws  Exception{
        String songid = reqMap.get("songid").toString();
        String savepath = "D:\\"+songid+".mp4";
        try {
            // 构造URL
            URL url = new URL("http://music.163.com/song/media/outer/url?id="+songid);
            // 打开URL连接
            URLConnection con = url.openConnection();
            // 得到URL的输入流
            InputStream input = con.getInputStream();
            // 设置数据缓冲
            byte[] bs = new byte[1024 * 2];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存图片至本地
            OutputStream os = new FileOutputStream(savepath);
            while ((len = input.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();
            input.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<Object,Object>  map = new HashMap<Object,Object>();
        String downloadurl = CommonUtil.GetProjectUrl(request)+"attach/tempfiledownload?filepath="+URLEncoder.encode(savepath);
        map.put("downloadurl",downloadurl);
        return map;
    }


}
