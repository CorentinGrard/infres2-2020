package serveur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServeurDB {
    private String url;

    public ServeurDB() {
        this.url = "jdbc:sqlite:./test.db";
        initialisation();
    }

    private void initialisation() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS message (\n" + "    id integer PRIMARY KEY,\n"
                + "    name text NOT NULL,\n" + "    message text NOT NULL\n" + ");";

        String sql2 = "CREATE TABLE IF NOT EXISTS user (\n" + "    name text PRIMARY KEY,\n"
                + "    salt text NOT NULL\n" + ");";

        try (Connection conn = this.connect(); Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createUser() {
        String sql = "INSERT INTO user VALUES(?,?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "toto");
            pstmt.setString(2, "sardoche");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String selectSalt(String name) {
        String sql = "SELECT salt FROM user WHERE name = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();

            return rs.getString("salt");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "null";
    }

    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void addMessage(String name, String message) {
        String sql = "INSERT INTO message(name,message) VALUES(?,?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}