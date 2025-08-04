package lucas.board.persistence.dao;

import lombok.AllArgsConstructor;
import lucas.board.dto.CardDetailsDTO;
import lucas.board.persistence.entity.CardEntity;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static lucas.board.persistence.converter.OffSetDateTimeConverter.toOffSetDateTime;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = """
                INSERT INTO CARDS (title, description, board_column_id) VALUES (?, ?, ?)
             """;

        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            var i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i, entity.getBoardColumEntity().getId());

            statement.executeUpdate();

            try (var rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
        }
        return entity;
    }


    public Optional<CardDetailsDTO> findById(Long id) throws SQLException {
        var sql = """
                    SELECT c.id, 
                    c.title,
                    c.description,
                    b.blocked_at,
                    b.block_reason,
                    c.board_column_id,
                    bc.name,
                    (SELECT COUNT(sub_b.id)
                            FROM BLOCKS sub_b 
                            WHERE sub_b.card_id = c.id) blocks_amount
                    FROM cards c
                    LEFT JOIN BLOCKS b 
                        ON c.id = b.card_id
                    AND b.unblocked_at IS NULL
                    INNER JOIN BOARD_COLUMNS bc
                        ON bc.id = c.board_column_id
                    WHERE c.id = ?; 
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.blocked_reason")),
                        toOffSetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.blocked_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }
}
