package com.myaushka.whatwishbot.telegram;

import com.myaushka.whatwishbot.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class WhatWishBotHeart {
    private static final String template = "AAAAAA, %s?";
    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(WhatWishBotHeart.class);

    @RequestMapping("/")
    public Greeting greeting(@RequestParam(value="name", required=false, defaultValue="WAZZZAAAAAAAAP!!!!") String name) {
        logger.info("Прилетел запрос");
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}
