package com.lzh.client2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.lzh")
@RefreshScope
public class Client2Application {

	public static void main(String[] args) {

		SpringApplication.run(Client2Application.class, args);
	}

}
