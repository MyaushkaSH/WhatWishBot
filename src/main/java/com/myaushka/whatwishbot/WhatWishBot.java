package com.myaushka.whatwishbot;


import com.myaushka.whatwishbot.constants.bot.BotMessageEnum;
import com.myaushka.whatwishbot.telegram.MessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.HashMap;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatWishBot extends SpringWebhookBot {
    private final Logger logger = LoggerFactory.getLogger(WhatWishBot.class);
    String botPath;
    String botUsername;
    String botToken;
    private HashMap<String, Integer> knownUsers = new HashMap<>();

    MessageHandler messageHandler;

    public WhatWishBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        logger.info("Получили сообщеньку, разбираем");
        logger.info(String.valueOf(update));
        try {
            return handleUpdate(update);
        } catch (IllegalArgumentException e) {
            logger.error("Не могу обработать сообщение, возвращаю отбивку");
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE.getMessage());
        } catch (NullPointerException e) {
            logger.error("Умные люди промолчат на это");
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage());
        } catch (Exception e) {
            logger.error("В первый раз такого страшного ипать придётся");
            e.printStackTrace();
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage());
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        long senderId = message.getFrom().getId();
        String firstName = message.getFrom().getFirstName();
        String userName = message.getFrom().getUserName();
        int messageId = message.getMessageId();
        String inputText = message.getText();
        Integer messageDate = message.getDate();
        boolean isSpam = false;
        if (!message.getNewChatMembers().isEmpty()) {
            logger.info("Похоже, тут у нас новый колека, обрабатываем");
            //long personalMessage = message.getChatId();
            knownUsers.put(userName, messageDate);
            logger.info("Записали: " + knownUsers);
            logger.info("К нам присоединился: " + firstName + " по времнеи в: " + messageDate +
                    "Писать ему: @" + userName);
            logger.info("Закончили обработку");
            return messageHandler.answerMessage(update.getMessage(), senderId);
        } else {
            if (!knownUsers.containsKey(userName)) {
                knownUsers.put(userName, messageDate);
                logger.info("Записали: " + knownUsers);
            } else {
                logger.info("Этого типочка уже знаем");
            }
            int joinDate = knownUsers.get(userName);
            int messageDiffTime = message.getDate() - joinDate;
            int minimalTime = 86400;
            logger.info("Разбираем сообщение от " + messageDiffTime);
            logger.info("Челик в чате уже: " + messageDiffTime + "секунд");
            logger.info("На текущий момент знаем этих товарищей: " + knownUsers);
            if (messageDiffTime < minimalTime) {
                logger.info("Кандидат на спамера");
                if (inputText.contains("http://") || inputText.contains("https://") || message.getForwardFrom() != null){
                    isSpam = true;
                } else {
                    logger.info("Похоже, не спамер, едем дальше");
                }
            } else {
                logger.info("Похоже что товарищ с нами уже давно");
            }

        }
        if (isSpam){
            String chatId = message.getChatId().toString();
            logger.info("Получили :" + inputText + "| Вот это");
            logger.info("Разгребаем то дерьмо, что получили от: " + senderId + ", который " + userName);
            logger.info("Удаляем спам");
            return deleteSpamMessage(chatId, messageId);
        } else {
            logger.info("Возвращаем ответ");
            return messageHandler.answerMessage(update.getMessage(), senderId);
        }
    }
    private DeleteMessage deleteSpamMessage(String chatId, int messageId) {
        return new DeleteMessage(chatId,messageId);

    }

}
