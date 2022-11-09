package com.aristocat.scribbly;

import com.aristocat.scribbly.bot.BotService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ScribblyApplication {

    private final BotService botService;

    public ScribblyApplication(BotService botService) {
        this.botService = botService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ScribblyApplication.class, args);
    }

    @PostConstruct
    public void initBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(botService);
    }


}
