package lucas.board.persistence.dao;

import lombok.AllArgsConstructor;
import lucas.board.dto.CardDetailsDTO;

import java.sql.Connection;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public CardDetailsDTO findById(Long id){
        return null;
    }
}
