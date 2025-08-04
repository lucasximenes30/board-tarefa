package lucas.board.persistence.entity;

import lombok.Data;

import static lucas.board.persistence.entity.BoardColumnKindEnum.INITIAL;

@Data
public class CardEntity {

    private Long id;
    private String title;
    private String description;
    private BoardColumEntity boardColumEntity = new BoardColumEntity();

}
