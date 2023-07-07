package com.myaushka.whatwishbot;


import com.myaushka.whatwishbot.constants.bot.BotMessageEnum;
import com.myaushka.whatwishbot.telegram.MessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.IOException;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatWishBot extends SpringWebhookBot {
    private final Logger logger = LoggerFactory.getLogger(WhatWishBot.class);
    String botPath;
    String botUsername;
    String botToken;

    MessageHandler messageHandler;

    public WhatWishBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        logger.info("Получили сообщеньку, разбираем");
        try {
            return handleUpdate(update);
        } catch (IllegalArgumentException e) {
            logger.error("Не могу обработать сообщение, возвращаю отбивку");
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE.getMessage());
        } catch (Exception e) {
            logger.error("В первый раз такого страшного ипать придётся");
            e.printStackTrace();
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage());
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) throws IOException {
        Message message = update.getMessage();
        long senderId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();
        logger.info("Разгребаем то дерьмо, что получили от: " + senderId + ", который " + userName);
        logger.info("Возвращаем ответ");
        return messageHandler.answerMessage(update.getMessage(), senderId);
    }

}
