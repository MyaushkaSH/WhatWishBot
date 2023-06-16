package com.myaushka.whatwishbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class WhatWishBotApplication {
	private static Logger logger = LoggerFactory.getLogger(WhatWishBotApplication.class);

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		InputStream is = Example.class.getClassLoader().getResourceAsStream("application.properties");
		properties.load(is);
		SpringApplication.run(WhatWishBotApplication.class, args);
		logger.info("Пошла инициализация");

	}

}
