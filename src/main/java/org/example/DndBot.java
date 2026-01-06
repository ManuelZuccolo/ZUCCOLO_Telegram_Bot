package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DndBot extends TelegramLongPollingBot {

    private final DnDBotHelper dndHelper = new DnDBotHelper(); // Istanza della nuova classe helper
    private String botUsername;
    private String botToken;

    public DndBot() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            botUsername = properties.getProperty("bot.username");
            botToken = properties.getProperty("bot.token");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore: impossibile leggere config.properties");
            botUsername = "DefaultBotUsername"; // fallback
            botToken = "DefaultBotToken";       // fallback
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();

            if (message.startsWith("/monster ")) {
                String monsterName = message.substring(9).trim(); // prende tutto dopo "/monster "
                if (monsterName.isEmpty()) {
                    sendMessage(update.getMessage().getChatId(), "Devi scrivere il nome del mostro!");
                    return;
                }

                try {
                    String json = DnDBotHelper.getMonsterData(monsterName);
                    Monster monster = new ObjectMapper().readValue(json, Monster.class);
                    sendMessage(update.getMessage().getChatId(), monster.toReadableString());


                } catch (Exception e) {
                    sendMessage(update.getMessage().getChatId(), "Errore nel recuperare il mostro: " + e.getMessage());
                }
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
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
}
