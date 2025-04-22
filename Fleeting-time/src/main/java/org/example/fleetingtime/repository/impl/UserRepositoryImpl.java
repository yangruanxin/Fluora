package org.example.fleetingtime.repository.impl;

import org.example.fleetingtime.bean.User;
import org.springframework.stereotype.Repository;
import org.example.fleetingtime.repository.UserRepository;

import java.sql.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final String JDBC_URL = "jdbc:mysql://mysql.sqlpub.com:3306/fleeting_time";
    private final String JDBC_USER = "raitocc";
    private final String JDBC_PASSWORD = "WTZor9EQYQpUS2Y4";

    @Override
    public User findByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM fleeting_time.users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO fleeting_time.users (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM fleeting_time.users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
