package lucas.board.service;

import lombok.AllArgsConstructor;
import lucas.board.persistence.dao.BoardColumnDAO;
import lucas.board.persistence.entity.BoardColumEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService {
    private final Connection connection;

    public Optional<BoardColumEntity> findById(final Long id) throws SQLException {
            var dao = new BoardColumnDAO(connection);
            return dao.findById(id);
    }
}
