package com.skillbox.cryptobot.bot;

import com.skillbox.cryptobot.client.BinanceClient;
import com.skillbox.cryptobot.repository.UserRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@Configuration
@EnableScheduling
public class CryptoBot extends TelegramLongPollingCommandBot {

    private final String botUsername;

    @Autowired
    BinanceClient binanceClient;

    @Autowired
    UserRepository userRepository;
    Map<Long, LocalDateTime> chatIdMap = new HashMap<>();

    @Value("${telegram.bot.notify.delay.value}")
    private int userDelay;


    public CryptoBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<IBotCommand> commandList
    ) {
        super(botToken);
        this.botUsername = botUsername;

        commandList.forEach(this::register);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }

    @PostConstruct
    @Scheduled(cron = "${check-delay}")
    public void sendMessage() throws IOException {
        System.out.println(binanceClient.getBitcoinPrice());

        Double price = binanceClient.getBitcoinPrice();

        List<Long> chatIdList = userRepository.getChatList(price);
        System.out.println(chatIdList.size());

        chatIdList.forEach(chatId -> {
            SendMessage message = new SendMessage(chatId.toString(), "Нужно покупать! Цена биткоина: " + TextUtil.toString(price) + "USD");
            if(!chatIdMap.containsKey(chatId) || LocalDateTime.now().minusMinutes(userDelay).isAfter(chatIdMap.get(chatId))) {
                chatIdMap.put(chatId, LocalDateTime.now());
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
