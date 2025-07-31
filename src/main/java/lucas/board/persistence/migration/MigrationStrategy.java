package lucas.board.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static lucas.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public void executeMigration() throws SQLException {
        var originalOut = System.out;
        var originalErr = System.err;

        try (var fos = new FileOutputStream("liquibase.log")) {
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));

            try (
                    var connection = getConnection();
                    var jdbcConnection = new JdbcConnection(connection)
            ) {
                String changelogFile = "db/changelog/db.changelog-master.xml";


                var resource = Thread.currentThread().getContextClassLoader().getResource(changelogFile);
                if (resource == null) {
                    System.err.println("❌ changelog NÃO encontrado no classpath: " + changelogFile);
                } else {
                    System.out.println("✅ changelog encontrado: " + changelogFile);
                }

                var liquibase = new Liquibase(
                        changelogFile,
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection
                );

                liquibase.update();
            } catch (SQLException | LiquibaseException e) {
                e.printStackTrace();
                System.setOut(originalErr);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
