package lucas.board.persistence.dao;

import lombok.RequiredArgsConstructor;
import lucas.board.persistence.entity.BoardColumEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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


    public List<BoardColumEntity> findByBoardId(Long id) throws SQLException {
        List<BoardColumEntity> boardColumEntities = new ArrayList<>();
        var sql = "SELECT id, name, `order`FROM BOARDS_COLUMNS WHERE board_id = ORDER BY `order`";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
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
}
