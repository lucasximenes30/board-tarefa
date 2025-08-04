package lucas.board.dto;

import lombok.AllArgsConstructor;
import lucas.board.persistence.entity.BoardColumnKindEnum;


public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind,
                             int cardsAmount) {
}
