package com.sunbasedata.sunbasedata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sunbasedata")
public class SunbasedataApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunbasedataApplication.class, args);
	}

}
