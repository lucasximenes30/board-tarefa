package lucas.board.persistence.dao;

import lombok.RequiredArgsConstructor;
import lucas.board.dto.BoardColumnDTO;
import lucas.board.persistence.entity.BoardColumEntity;
import lucas.board.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static lucas.board.persistence.entity.BoardColumnKindEnum.findByName;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumEntity insert(final BoardColumEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARD_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i++, entity.getBoard().getId());
            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
        return entity;
    }


    public List<BoardColumEntity> findByBoardId(Long boardId) throws SQLException {
        List<BoardColumEntity> boardColumEntities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, kind FROM BOARD_COLUMNS WHERE board_id = ? ORDER BY `order`;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var entity = new BoardColumEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                boardColumEntities.add(entity);
            }
        return boardColumEntities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = """
        SELECT bc.id,
               bc.name,
               bc.kind,
               (SELECT COUNT(c.id)
                FROM CARDS c
                WHERE c.board_column_id = bc.id) cards_amount
        FROM BOARD_COLUMNS bc
        WHERE board_id = ?
        ORDER BY `order`;
    """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
        }

        return dtos;
    }
    public Optional<BoardColumEntity> findById(Long boardId) throws SQLException {
        List<BoardColumEntity> boardColumEntities = new ArrayList<>();
        var sql = """
                SELECT 
                bc.name, bc.kind, c.id, c.title, c.description 
                FROM BOARD_COLUMNS bc
                LEFT JOIN CARDS c 
                ON c.board_column_id = bc.id
                WHERE bc.id = ?;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()){
                var entity = new BoardColumEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setKind(findByName(resultSet.getString("bc.kind")));
                do{
                    if(isNull(resultSet.getString("c.title"))){
                        break;
                    }
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                }while (resultSet.next());
                return Optional.of(entity);

            }

            return Optional.empty();
        }
    }

}
