package lucas.board.persistence.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionConfig {

    @SneakyThrows
    public static Connection getConnection() {
        var url = "jdbc:mysql://localhost:3306/board_db";
        var user = "root";
        var password = "12345678";
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
