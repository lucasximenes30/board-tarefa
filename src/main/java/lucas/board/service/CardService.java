package lucas.board.service;

import lombok.AllArgsConstructor;
import lucas.board.persistence.dao.CardDAO;
import lucas.board.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity cardEntity)throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(cardEntity);
            connection.commit();
            return cardEntity;
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }
}
