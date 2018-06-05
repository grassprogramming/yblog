package com.blog;

import com.blog.entity.Sys_Config;
import com.blog.util.CommonDao;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;
@EnableTransactionManagement
@SpringBootApplication
public class BlogApplication {

	private static final Logger logger = LoggerFactory.getLogger(BlogApplication.class);
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BlogApplication.class, args);
		logger.info("testinfo");
	}

	@Bean
	public PageHelper pageHelper() {
		PageHelper pageHelper = new PageHelper();
		Properties p = new Properties();
		p.setProperty("offsetAsPageNum", "true");
		p.setProperty("rowBoundsWithCount", "true");
		p.setProperty("reasonable", "true");
		pageHelper.setProperties(p);
		return pageHelper;
	}


}
