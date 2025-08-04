package lucas.board.ui;

import lucas.board.persistence.entity.BoardColumEntity;
import lucas.board.persistence.entity.BoardColumnKindEnum;
import lucas.board.persistence.entity.BoardEntity;
import lucas.board.service.BoardQueryService;
import lucas.board.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static lucas.board.persistence.config.ConnectionConfig.getConnection;
import static lucas.board.persistence.entity.BoardColumnKindEnum.CANCEL;
import static lucas.board.persistence.entity.BoardColumnKindEnum.INITIAL;
import static lucas.board.persistence.entity.BoardColumnKindEnum.PENDING;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute () throws SQLException {
        System.out.println("Bem vindo ao gerencciador de boards, escolha a opção desejada");
        var option = -1;
        while(true){
            System.out.println("1 - criar um novo board");
            System.out.println("2 - selecionar um board existente");
            System.out.println("3 - excluir um board");
            System.out.println("4 - sair");
            option = scanner.nextInt();
            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }
    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Digite o nome do novo board");
        entity.setName(scanner.next());
        System.out.println("Seu board terá colunas além das 3 padrões? Se sim, informe quantas, se não, digite '0' ");
        var addtionalColumns = scanner.nextInt();

        List<BoardColumEntity> columns = new ArrayList<>();
        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < addtionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }
        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, INITIAL, addtionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, addtionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);

        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }
    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        var connection = getConnection();
        var queryService = new BoardQueryService(connection);
        var optional = queryService.findById(id);
        optional.ifPresentOrElse(
                board -> {
                    try {
                        new BoardMenu(board).execute();
                    } catch (SQLException e) {
                        System.out.println("Erro ao abrir o menu do board: " + e.getMessage());
                    }
                },
                () -> System.out.println("Não foi encontrado")
        );

    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que será excluido");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)) {

                System.out.printf("Board com id %s foi deletado com sucesso!\n", id);
            }else {
                System.out.printf("O board com id %s não foi encontrado.\n", id);
            }
        }
    }
    private BoardColumEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

}
