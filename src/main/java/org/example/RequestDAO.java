package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RequestDAO {

    public static void logRequest(long chatId, String monsterIndex) {
        String sql = """
            INSERT INTO monster_requests (chat_id, monster_index)
            VALUES (?, ?);
        """;

        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, chatId);
            ps.setString(2, monsterIndex);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
