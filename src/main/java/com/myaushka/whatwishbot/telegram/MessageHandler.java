package com.myaushka.whatwishbot.telegram;


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

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
        String inputText = message.getText();

        logger.debug("Обрабатываем сообщение: " + inputText);
        if (inputText == null) {
            logger.error("Нету текста в этом сообщении");
            throw new IllegalArgumentException();
        } else if (inputText.equals("/start")) {
            logger.info("Обрабатываем команду Start");
            return getStartMessage(chatId);
        } else if (inputText.equals("/whatwish")) {
            logger.info("Возвращаем смысл жизни");
            return getWhatMessage(chatId);
        } else {
            logger.info("Ничего не нашлось, шлём куда надо");
            return getOoMessage(chatId);
        }
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.START_MESSAGE.getMessage());
        return sendMessage;
    }

    private SendMessage getWhatMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
        return sendMessage;
    }

    private SendMessage getOoMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.OO_MESSAGE.getMessage());
        return sendMessage;
    }
}
