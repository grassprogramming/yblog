package com.blog.util;

import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;

/**
 * Created by paul on 2018/7/17.
 */
@Component
public class MailUtil {
    @Autowired
    private Environment env;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private FreeMarkerConfigurer configurer;
    public static MailUtil mailUtil;
    @PostConstruct
    public void init() {
        mailUtil = this;
    }

    public void SenTextMail(String reciverMail,String subject,String Content) throws Exception{
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(Content);
        message.setFrom( mailUtil.env.getProperty("spring.mail.username"));
        message.setTo(reciverMail);
        message.setSentDate(new Date());
        mailUtil.mailSender.send(message);
    }

    public void SenHtmlMail(String reciverMail,String subject,String Content) throws Exception{
        MimeMessage message = mailUtil.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(subject);
        helper.setText(Content,true);
        helper.setFrom( mailUtil.env.getProperty("spring.mail.username"));
        helper.setTo(reciverMail);
        helper.setSentDate(new Date());
        mailUtil.mailSender.send(message);
    }

    public void SenHtmlMail_FMTemplate(String reciverMail, String subject, String templateName, Map<String, Object> templateModal) throws Exception{
        Template template = mailUtil.configurer.getConfiguration().getTemplate(templateName);
        String Content = FreeMarkerTemplateUtils.processTemplateIntoString(template, templateModal);
        SenHtmlMail(reciverMail,subject,Content);
    }
}
