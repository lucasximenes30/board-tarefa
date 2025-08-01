package lucas.board.persistence.entity;

import lombok.Data;

@Data
public class BoardColumEntity {

    private Long id;
    private String name;
    private int order;
    private BoardColumKindEnum kind;
}
