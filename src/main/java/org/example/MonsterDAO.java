package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MonsterDAO {

    public static Optional<String> getMonsterJson(String index) {
        String sql = "SELECT json_data FROM monsters WHERE monster_index = ?";

        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, index);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getString("json_data"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static void saveMonster(String index, String name, String json) {
        String sql = """
            INSERT OR REPLACE INTO monsters (monster_index, name, json_data, last_update)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP);
        """;

        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, index);
            ps.setString(2, name);
            ps.setString(3, json);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
