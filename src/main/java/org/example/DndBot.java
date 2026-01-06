package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.InputStream;
import java.util.Properties;

public class DndBot extends TelegramLongPollingBot {

    private String botToken;
    private String botUsername;

    public DndBot() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            botToken = prop.getProperty("BOT_TOKEN");
            botUsername = prop.getProperty("BOT_USERNAME");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));

            if (messageText.equals("/start")) {
                message.setText("Bot D&D. Pronto per la tua prossima avventura");
            } else {
                message.setText("Non conosco questo comando. Prova /start.");
            }

            try {
                execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
