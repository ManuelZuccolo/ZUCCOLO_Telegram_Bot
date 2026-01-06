package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DndBot extends TelegramLongPollingBot {

    private final DnDBotHelper dndHelper = new DnDBotHelper(); // Istanza della nuova classe helper

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
                    // Chiamata al helper per ottenere le info del mostro
                    String monsterInfo = DnDBotHelper.getMonsterData(monsterName);
                    sendMessage(update.getMessage().getChatId(), monsterInfo);
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
        return "DnD_InfosBot";
    }

    @Override
    public String getBotToken() {
        return "8166957016:AAHiRZbjRLELpxn7bxeltE7rtjnKLMxwgaQ";
    }
}
