package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DndBot extends TelegramLongPollingBot {

    private final DnDBotHelper dndHelper = new DnDBotHelper();
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
            botUsername = "DefaultBotUsername"; //fallback
            botToken = "DefaultBotToken";       //fallback
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        // --- GESTIONE CALLBACK DEI PULSANTI ---
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            // Se il callback è del tipo /monster <nome>
            if (callbackData.startsWith("/monster ")) {
                String monsterName = callbackData.substring(9).trim();
                try {
                    String index = DnDBotHelper.nameToIndex(monsterName);
                    ObjectMapper mapper = new ObjectMapper();
                    String json;

                    // Legge dal database
                    Optional<String> cached = MonsterDAO.getMonsterJson(index);
                    if (cached.isPresent()) {
                        json = cached.get();
                    } else {
                        // Se non c'è chiama API
                        json = DnDBotHelper.getMonsterData(monsterName);

                        // Salva nel database
                        Monster tmp = mapper.readValue(json, Monster.class);
                        MonsterDAO.saveMonster(index, tmp.name, json);
                    }

                    // Parsing
                    Monster monster = mapper.readValue(json, Monster.class);

                    // Invia immagine se disponibile
                    if (monster.image != null && !monster.image.isEmpty()) {
                        String fullUrl = monster.image.startsWith("http")
                                ? monster.image
                                : "https://www.dnd5eapi.co" + monster.image;

                        SendPhoto photo = new SendPhoto();
                        photo.setChatId(String.valueOf(chatId));
                        photo.setPhoto(new InputFile(fullUrl));
                        execute(photo);
                    }

                    // Invia testo con le statistiche
                    sendMessage(chatId, monster.toReadableString());

                    // Registra richiesta nel DB
                    RequestDAO.logRequest(chatId, index);

                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(chatId, "Errore nel recuperare il mostro: " + e.getMessage());
                }
            }

            return; // Evita di processare il messaggio come testo normale
        }
/// ////////////////////////////

        if (update.hasMessage() && update.getMessage().hasText()) {
            UserDAO.upsertUser(update.getMessage().getFrom());

            String message = update.getMessage().getText().trim();

            if(message.equals("/start")){
                String welcome = """
                🎲 Benvenuto nel DnD Monster Bot!
            
                Questo bot ti permette di cercare mostri di Dungeons & Dragons 5e
                e ottenere statistiche complete direttamente da Telegram.
            
                📌 Comandi disponibili:
                • /monster <nome> → mostra le informazioni di un mostro
                  (es: /monster goblin)
                • /topmonsters → mostra i 5 mostri più cercati
                • /fav <nome> → salva il mostro nei preferiti
                • /unfav <nome> → rimuove il mostro dai preferiti
                • /favourites → mostra i mostri salvati come preferiti
                • /stats → statistiche generali del bot
                • /help → mostra i comandi disponibili
            
                Buona avventura! 🐉
                """;

                sendMessage(update.getMessage().getChatId(), welcome);
                return;
            }else

            if(message.equals("/help")){
                String help = """
                📌 Comandi disponibili:
                • /monster <nome> → mostra le informazioni di un mostro
                  (es: /monster goblin)
                • /topmonsters → mostra i 5 mostri più cercati
                • /fav <nome> → salva il mostro nei preferiti
                • /unfav <nome> → rimuove il mostro dai preferiti
                • /favourites → mostra i mostri salvati come preferiti
                • /stats → statistiche generali del bot
            
                """;

                sendMessage(update.getMessage().getChatId(), help);
                return;
            }else
            if (message.startsWith("/monster ")) {
                try {
                    String monsterName = message.substring(9).trim();
                    String index = DnDBotHelper.nameToIndex(monsterName);
                    ObjectMapper mapper = new ObjectMapper();
                    String json;

                    //Legge dal database
                    Optional<String> cached = MonsterDAO.getMonsterJson(index);
                    if (cached.isPresent()) {
                        json = cached.get();
                    } else {
                        //Se non c'è chiama API
                        json = DnDBotHelper.getMonsterData(monsterName);

                        //Salva nel database
                        Monster tmp = mapper.readValue(json, Monster.class);
                        MonsterDAO.saveMonster(index, tmp.name, json);
                    }

                    // Parsing
                    Monster monster = mapper.readValue(json, Monster.class);

                    //Immagine
                    if (monster.image != null && !monster.image.isEmpty()) {
                        String fullUrl = monster.image.startsWith("http")
                                ? monster.image
                                : "https://www.dnd5eapi.co" + monster.image;

                        SendPhoto photo = new SendPhoto();
                        photo.setChatId(update.getMessage().getChatId().toString());
                        photo.setPhoto(new InputFile(fullUrl));

                        try {
                            execute(photo);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                    //Testo
                    sendMessage(update.getMessage().getChatId(), monster.toReadableString());

                    //Registra nel DB
                    RequestDAO.logRequest(update.getMessage().getChatId(), monster.index);

                } catch (Exception e) {
                    sendMessage(update.getMessage().getChatId(),
                            "Errore nel recuperare il mostro: " + e.getMessage());
                }
            }
            else
            if (message.startsWith("/topmonsters")) {
                try {
                    StringBuilder sb = new StringBuilder("🏆 Mostri più cercati:\n");

                    String sql = """
                        SELECT m.name, COUNT(r.id) AS searches
                        FROM monster_requests r
                        JOIN monsters m ON r.monster_index = m.monster_index
                        GROUP BY r.monster_index
                        ORDER BY searches DESC
                        LIMIT 5;
                        """;

                    try (Connection c = DatabaseManager.getConnection();
                         PreparedStatement ps = c.prepareStatement(sql);
                         ResultSet rs = ps.executeQuery()) {

                        boolean hasResults = false;
                        while (rs.next()) {
                            hasResults = true;
                            sb.append(" - ").append(rs.getString("name"))
                                    .append(" (").append(rs.getInt("searches")).append(" ricerche)\n");
                        }

                        if (!hasResults) sb.append("Nessuna ricerca registrata al momento.");

                        sendMessage(update.getMessage().getChatId(), sb.toString());
                    }

                } catch (Exception e) {
                    sendMessage(update.getMessage().getChatId(),
                            "Errore nel recuperare le statistiche: " + e.getMessage());
                }
            }else

            if (message.startsWith("/stats")) {
                try {
                    int totalMonsters = 0;
                    int totalRequests = 0;

                    try (Connection c = DatabaseManager.getConnection();
                         Statement stmt = c.createStatement()) {

                        ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM monsters;");
                        if (rs1.next()) totalMonsters = rs1.getInt("cnt");

                        ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM monster_requests;");
                        if (rs2.next()) totalRequests = rs2.getInt("cnt");
                    }

                    String statsMsg = """
                                    📊 Statistiche Bot:
                                    - Mostri nel DB: %d
                                    - Ricerche totali registrate: %d
                                    """.formatted(totalMonsters, totalRequests);

                    sendMessage(update.getMessage().getChatId(), statsMsg);

                } catch (Exception e) {
                    sendMessage(update.getMessage().getChatId(),
                            "Errore nel recuperare le statistiche: " + e.getMessage());
                }
            }else
            if (message.startsWith("/fav ")) {
                try {
                    String monsterName = message.substring(5).trim();
                    String index = DnDBotHelper.nameToIndex(monsterName);

                    FavouriteDAO.addFavorite(
                            update.getMessage().getChatId(),
                            index
                    );

                    sendMessage(
                            update.getMessage().getChatId(),
                            "⭐ Mostro aggiunto ai preferiti: " + monsterName
                    );

                } catch (Exception e) {
                    sendMessage(
                            update.getMessage().getChatId(),
                            "Errore nell'aggiunta ai preferiti."
                    );
                }
            }
            else if (message.startsWith("/unfav ")) {
                try {
                    String monsterName = message.substring(7).trim();
                    String index = DnDBotHelper.nameToIndex(monsterName);

                    FavouriteDAO.removeFavorite(
                            update.getMessage().getChatId(),
                            index
                    );

                    sendMessage(
                            update.getMessage().getChatId(),
                            "❌ Mostro rimosso dai preferiti: " + monsterName
                    );

                } catch (Exception e) {
                    sendMessage(
                            update.getMessage().getChatId(),
                            "Errore nella rimozione dai preferiti."
                    );
                }
            }
            else if (message.equals("/favourites"))
            {
                try (Connection c = DatabaseManager.getConnection();
                     PreparedStatement ps = c.prepareStatement(
                             "SELECT m.name FROM favorites f JOIN monsters m ON f.monster_index = m.monster_index WHERE f.chat_id = ?")) {

                    ps.setLong(1, update.getMessage().getChatId());
                    ResultSet rs = ps.executeQuery();

                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    boolean hasFavorites = false;

                    while (rs.next()) {
                        hasFavorites = true;
                        String monsterName = rs.getString("name");

                        InlineKeyboardButton button = new InlineKeyboardButton();
                        button.setText(monsterName);
                        button.setCallbackData("/monster " + monsterName);

                        List<InlineKeyboardButton> row = new ArrayList<>();
                        row.add(button);
                        rows.add(row);
                    }

                    SendMessage messageToSend = new SendMessage();
                    messageToSend.setChatId(update.getMessage().getChatId().toString());

                    if (!hasFavorites) {
                        messageToSend.setText("Non hai ancora mostri preferiti. Usa /fav <nome> per aggiungerne qualcuno.");
                        execute(messageToSend);
                    } else {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                        markup.setKeyboard(rows);
                        messageToSend.setText("I tuoi mostri preferiti:");
                        messageToSend.setReplyMarkup(markup);
                        execute(messageToSend);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(update.getMessage().getChatId(), "Errore nel recuperare i preferiti: " + e.getMessage());
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

    private void sendPhoto(Long chatId, Monster monster) {
        if (monster.image != null && !monster.image.isEmpty()) {
            try {
                String fullUrl = monster.image.startsWith("http") ? monster.image
                        : "https://www.dnd5eapi.co" + monster.image;

                //A quanto pare Telegram vuole InputFile per URL e noi possiamo solo che soffisfarlo
                org.telegram.telegrambots.meta.api.objects.InputFile inputFile = new org.telegram.telegrambots.meta.api.objects.InputFile(fullUrl);

                SendPhoto photo = new SendPhoto();
                photo.setChatId(chatId.toString());
                photo.setPhoto(inputFile);
                photo.setCaption(monster.toReadableString());

                execute(photo);
                System.out.println("Sending image: " + fullUrl);

            } catch (Exception e) {
                e.printStackTrace();
                // fallback: invia solo testo se errore
                sendMessage(chatId, monster.toReadableString());
            }
        } else {
            sendMessage(chatId, monster.toReadableString());
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
