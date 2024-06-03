package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.model.User;
import com.skillbox.cryptobot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
public class SubscribeCommand implements IBotCommand {

    @Autowired
    UserRepository userRepository;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());


        String[] text = message.getText().split("\s");
        Double price = Double.parseDouble(text[1].replace(',', '.'));

        answer.setText("Новая подписка создана на стоимость " + price);

        userRepository.update(price, message.getChatId());

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /subscribe command", e);
        }

    }
}