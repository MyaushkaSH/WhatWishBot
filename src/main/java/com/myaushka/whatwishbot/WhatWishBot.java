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
import com.myaushka.whatwishbot.config.TelegramConfig;

import java.util.HashMap;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatWishBot extends SpringWebhookBot {
    private final Logger logger = LoggerFactory.getLogger(WhatWishBot.class);
    private final TelegramConfig telegramConfig;

    String botPath;
    String botUsername;
    String botToken;

    private HashMap<String, Integer> knownUsers = new HashMap<>();

    MessageHandler messageHandler;

    public WhatWishBot(SetWebhook setWebhook, TelegramConfig telegramConfig, MessageHandler messageHandler) {
        super(setWebhook);
        this.telegramConfig = telegramConfig;
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
            logger.error("Умные люди промолчат на это ");
            e.printStackTrace();
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage());
        } catch (Exception e) {
            logger.error("В первый раз такого страшного ипать придётся");
            e.printStackTrace();
            return null;
            /*new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_WHAT_THE_FUCK.getMessage());*/
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
        boolean isForwarded = false;
        String messageType = "";
        if (message.getForwardDate() != null) {
            logger.info("Это сообщение переслали");
            isForwarded = true;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            logger.info("Похоже, поймали сообщение " + update.hasMessage());
            messageType = "Text";
        } else {
            logger.info("Это не совсем сообщенька, обрабатываем по-другому");
        }

        //Примитивный антиспам
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
            int minimalTime = telegramConfig.getAntispamTimer();
            logger.info("Антиспам тайменр у нас такой: " + minimalTime);
            logger.info("Разбираем сообщение от " + userName);
            logger.info("Челик в чате уже: " + messageDiffTime + "секунд");
            logger.info("На текущий момент знаем этих товарищей: " + knownUsers);
            logger.info("Смотрим ещё на это поле: " + message.getForwardFrom());
            if (messageDiffTime < minimalTime) {
                logger.info("Кандидат на спамера");
                if (isForwarded) {
                    logger.info("Переслал сообщение");
                    isSpam = true;
                } else if (messageType.equals("Text")) {
                    logger.info("Проверяем по тексту");
                    if (inputText.contains("http://") || inputText.contains("https://")){
                        logger.info("Попался на ссылке");
                        isSpam = true;
                    } else {
                        logger.info("Обработали тескт, кажись не спамер");
                    }
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
        } else if (messageType.equals("Text")) {
            logger.info("Возвращаем ответ на текст");
            return messageHandler.answerMessage(update.getMessage(), senderId);
        } else {
            logger.info("Возвращаем ответ не на текст");
            return messageHandler.answerMessage(update.getMessage(), senderId);
        }
    }
    private DeleteMessage deleteSpamMessage(String chatId, int messageId) {
        return new DeleteMessage(chatId,messageId);

    }

}
