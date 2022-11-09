package com.aristocat.scribbly.bot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;

@Service
public class BotService extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(BotService.class);

    @Value("${secrets.telegram-bot}")
    @Autowired
    private String telegramBotKey;

    @Value("${secrets.api-key}")
    @Autowired
    private String apiKey;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().isUserMessage()) {
            try {
                this.createAResponse(update);
            } catch (TelegramApiException | IOException e) {
                logger.error("Error while receiving updates", e);
            }
        }
    }

    private void createAResponse(Update update) throws TelegramApiException, IOException {
        logger.info("message received from {} to create '{}'", update.getMessage().getFrom().getFirstName(), update.getMessage().getText());
        var message = new SendMessage();
        message.setParseMode("HTML");
        message.setChatId(update.getMessage().getChatId().toString());
        if (update.getMessage().getText().startsWith("/start")) {
            message.setText("Describe anything and AI creates a digital art from the description. \n\nTry something like: <code>'A boy with balloon'</code>");
            execute(message);
        } else {
            String url = requestOpenAPI(update.getMessage().getText());
            sendAnImage(url, update.getMessage().getChatId());
        }
    }

    private void sendAnImage(String imageResource, long chatId) throws IOException, TelegramApiException {
        var imageMessage = new SendPhoto();
        var inputFile = new InputFile();
        InputStream input = new URL(imageResource).openStream();
        inputFile.setMedia(input, "scribbly_ai_generated_" + Instant.now() + ".png");
        imageMessage.setChatId(chatId);
        imageMessage.setPhoto(inputFile);
        execute(imageMessage);
    }


    private String buildMessageHtml(String url) {
        return """
                <a href = '%s'> Open link </a>
                """.formatted(url);
    }

    public String requestOpenAPI(String prompt) {
        String some = """
                {
                    "prompt": "%s ,digital art",
                    "n": 1,
                    "size": "512x512"
                  }
                """.formatted(prompt);
        return WebClient.create("https://api.openai.com/v1/images/generations").post().header("Content-Type", "application/json").header("Authorization", apiKey).bodyValue(some).retrieve().bodyToMono(String.class).map(this::getUrlFromResponsePayload).onErrorReturn("Failed to create an image").block();
    }

    private String getUrlFromResponsePayload(String json) {
        JSONObject obj = new JSONObject(json);
        JSONArray arr = obj.getJSONArray("data");
        for (int i = 0; i < arr.length(); i++) {
            return arr.getJSONObject(i).getString("url");
        }
        return "";
    }

    @Override
    public String getBotUsername() {
        return "scribbly_ai_bot";
    }

    @Override
    public String getBotToken() {
        return telegramBotKey;
    }


}
