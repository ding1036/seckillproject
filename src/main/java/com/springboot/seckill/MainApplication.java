package com.springboot.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MainApplication
		//extends SpringBootServletInitializer   /*change output to war*/
{
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
//change output to war
	/*@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder bulider){
		return bulider.sources(MainApplication.class);
	}*/
}
