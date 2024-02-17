package com.myaushka.whatwishbot.config;

import com.myaushka.whatwishbot.WhatWishBot;
import com.myaushka.whatwishbot.telegram.MessageHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private final TelegramConfig telegramConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public WhatWishBot springWebhookBot(SetWebhook setWebhook,
                                        MessageHandler messageHandler) {
        WhatWishBot bot = new WhatWishBot(setWebhook, telegramConfig, messageHandler);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }
}
