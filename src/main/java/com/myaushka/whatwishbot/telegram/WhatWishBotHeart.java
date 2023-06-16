package com.myaushka.whatwishbot.telegram;

import com.myaushka.whatwishbot.Greeting;
import com.myaushka.whatwishbot.WhatWishBot;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@AllArgsConstructor
public class WhatWishBotHeart {
    private final WhatWishBot whatWishBot;
    //private final AtomicLong counter = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(WhatWishBotHeart.class);

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return whatWishBot.onWebhookUpdateReceived(update);
    }

}
