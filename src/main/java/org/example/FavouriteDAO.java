package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavouriteDAO {

    public static void addFavorite(long chatId, String monsterIndex) {
        String sql = """
            INSERT OR IGNORE INTO favorites (chat_id, monster_index)
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

    public static void removeFavorite(long chatId, String monsterIndex) {
        String sql = """
            DELETE FROM favorites
            WHERE chat_id = ? AND monster_index = ?;
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

    public static List<String> getUserFavorites(long chatId) {
        String sql = """
            SELECT monster_index
            FROM favorites
            WHERE chat_id = ?
            ORDER BY added_at DESC;
        """;

        List<String> favorites = new ArrayList<>();

        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, chatId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                favorites.add(rs.getString("monster_index"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favorites;
    }
}
