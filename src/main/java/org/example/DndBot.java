package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

public class DndBot extends TelegramLongPollingBot {

    private String botToken;
    private String botUsername;

    public DndBot() {
        // Carica token e username dal file config.properties
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
                message.setText("Ciao! Sono il tuo bot D&D. Pronto per avventure epiche!");
            } else if (messageText.toLowerCase().startsWith("/monster ")) {
                String[] parts = messageText.split(" ", 2);
                if (parts.length < 2) {
                    message.setText("Devi specificare il nome del mostro. Esempio: /monster goblin");
                } else {
                    String monsterName = parts[1].toLowerCase();
                    String monsterInfo = getMonsterInfo(monsterName);
                    message.setText(monsterInfo);
                }
            } else {
                message.setText("Non conosco questo comando. Prova /start o /monster <nome>");
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per prendere info da D&D API
    private String getMonsterInfo(String name) {
        String apiUrl = "https://www.dnd5eapi.co/api/monsters/" + name;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "Mostro non trovato o errore API!";
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject obj = new JSONObject(inline.toString());

            // Costruisco una risposta leggibile
            StringBuilder sb = new StringBuilder();
            sb.append("Nome: ").append(obj.getString("name")).append("\n");
            sb.append("Tipo: ").append(obj.getString("type")).append("\n");
            sb.append("Taglia: ").append(obj.getString("size")).append("\n");
            sb.append("Punti Ferita: ").append(obj.getJSONObject("hit_points")).append("\n");
            sb.append("CA: ").append(obj.getInt("armor_class")).append("\n");

            // Forse aggiungiamo le abilità
            if (obj.has("actions")) {
                sb.append("Azioni:\n");
                JSONArray actions = obj.getJSONArray("actions");
                for (int i = 0; i < actions.length(); i++) {
                    JSONObject action = actions.getJSONObject(i);
                    sb.append("- ").append(action.getString("name")).append(": ");
                    if (action.has("desc")) sb.append(action.getString("desc"));
                    sb.append("\n");
                }
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Errore durante la connessione all'API D&D.";
        }
    }
}
