package com.blog.controller;

import com.blog.entity.Sys_Config;
import com.blog.spider.pageprocessor.WYCloudMusicPageProcessor;
import com.blog.util.PageBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import us.codecraft.webmagic.Spider;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2018/6/29.
 */
@RestController
public class AttachController {

    @RequestMapping("/attach")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }


    @RequestMapping(value="/attach/tempfiledownload",method= RequestMethod.GET)
    @ResponseBody
    public void tempfiledownload(HttpServletRequest request,HttpServletResponse response) {
      try {
            String filepath = URLDecoder.decode(request.getParameter("filepath"));
            File downloadFile = new File(filepath);
            ServletContext context = request.getServletContext();

            // get MIME type of the file
            String mimeType = context.getMimeType(filepath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
                System.out.println("context getMimeType is null");
            }
            System.out.println("MIME type: " + mimeType);

            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"",
                    downloadFile.getName());
            response.setHeader(headerKey, headerValue);

            // Copy the stream to the response's output stream.
            InputStream myStream = new FileInputStream(filepath);
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
