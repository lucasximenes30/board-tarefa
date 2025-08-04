package lucas.board;

import lucas.board.persistence.migration.MigrationStrategy;
import lucas.board.ui.MainMenu;

import java.sql.SQLException;

import static lucas.board.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}
