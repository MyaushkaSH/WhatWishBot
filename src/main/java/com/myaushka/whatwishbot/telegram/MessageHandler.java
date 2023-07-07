package com.myaushka.whatwishbot.telegram;


import com.plexpt.chatgpt.ChatGPT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import com.myaushka.whatwishbot.constants.bot.BotMessageEnum;
import com.myaushka.whatwishbot.config.TelegramConfig;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    private final TelegramConfig telegramConfig;
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    public BotApiMethod<?> answerMessage(Message message, long senderId) {
        String chatId = message.getChatId().toString();
        String inputText = message.getText();
        int messageId = message.getMessageId();

        logger.debug("Обрабатываем сообщение: " + inputText);
        if (inputText == null) {
            logger.error("Нету текста в этом сообщении");
            throw new IllegalArgumentException();
        } if (inputText.equals("/start")) {
            logger.info("Обрабатываем команду Start");
            return getStartMessage(chatId);
        } else if (inputText.equals("/whatwish")) {
            logger.info("Запрашиваем смысл жизни");
            return getWhatMessage(chatId);
        } else if (inputText.contains("@WhatGWishbot ,")) {
            logger.info("Обрабатываем сообщение в ChatGPT");
            String messageText = inputText.substring(inputText.indexOf(",") + 1);
            return getChatGptAnswer(chatId, messageText, messageId);
        } else {
            logger.info("Ничего не нашлось, шлём куда надо");
            return getOoMessage(chatId);
        }
    }
    private SendMessage getChatGptAnswer(String chatId, String inputText, int messageId) {
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(telegramConfig.getApiKey())
                .build()
                .init();
        String res = chatGPT.chat(inputText);
        logger.info(res);
        SendMessage sendMessage= new SendMessage(chatId, res);
        sendMessage.setReplyToMessageId(messageId);
        return sendMessage;
    }

    private SendMessage getStartMessage(String chatId) {
        return new SendMessage(chatId, BotMessageEnum.START_MESSAGE.getMessage());
    }

    private SendMessage getWhatMessage(String chatId) {
        return new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
    }

    private SendMessage getOoMessage(String chatId) {
        return new SendMessage(chatId, BotMessageEnum.OO_MESSAGE.getMessage());
    }
}
