package com.lzh.provider1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.lzh")
//@RefreshScope
public class Provider1Application {
	public static Logger logger = LoggerFactory.getLogger(Provider1Application.class);
	public static void main(String[] args) {
		logger.info("开始启动系统");
		SpringApplication.run(Provider1Application.class, args);
		logger.error("启动成功");
	}
}
