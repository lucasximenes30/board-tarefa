package lucas.board.ui;

import lombok.AllArgsConstructor;
import lucas.board.persistence.entity.BoardColumEntity;
import lucas.board.persistence.entity.BoardEntity;
import lucas.board.service.BoardColumnQueryService;
import lucas.board.service.BoardQueryService;
import lucas.board.service.CardQueryService;

import java.sql.SQLException;
import java.util.Scanner;

import static lucas.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {
    private BoardEntity boardEntity;

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException{
        System.out.printf("Bem vindo ao board %s, selecione a operação desejada", boardEntity.getId());
        var option = -1;
        while(option != 9){
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Ver board");
            System.out.println("7 - Ver Coluna com cards");
            System.out.println("8 - Ver card");
            System.out.println("9 - Voltar para o menu anterior");
            System.out.println("10 - sair");
            option = scanner.nextInt();
            switch (option){
                case 1 -> createCard();
                case 2 -> moveCardToNextColumn();
                case 3 -> blockCard();
                case 4 -> unblockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn();
                case 8 -> showCard();
                case 9 -> System.out.println("Voltando para o menu anterior.");
                case 10 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private void createCard() {
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(boardEntity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });
        } catch (Exception e) {
            System.out.println("Erro ao exibir o board: " + e.getMessage());
        }
    }


    private void showColumn() throws SQLException {
        var columnIds = boardEntity.getBoardColumns()
                .stream()
                .map(BoardColumEntity::getId)
                .toList();

        var selectedColumn = 1L;
        while (!columnIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s \n", boardEntity.getName());
            boardEntity.getBoardColumns().forEach(c ->
                    System.out.printf("%s - %s [%s] \n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s \n", co.getName(), co.getKind());
                co.getCards().forEach(ca ->
                        System.out.printf("Card %s - %s.\nDescrição: %s\n", ca.getId(), ca.getTitle(), ca.getDescription())
                );
            });
        }
    }



    private void showCard() throws SQLException {

        System.out.println("Informe o Id do card que deseja visualizar: ");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection)
                    .findById(selectedCardId)
                    .ifPresentOrElse(c ->{
                                System.out.printf("Card %s - %s.\n",c.id(),c.title());
                                System.out.printf("Descrição: %s\n",c.description());
                                System.out.println(c.blocked()
                                        ? "Está bloqueado. Motivo:"+ c.blockReason() :
                                        "Não está bloqueado.") ;
                                System.out.printf("Já foi bloqueado %s vezes", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                    },
                            () -> System.out.printf("Não existe um card com o id %s \n", selectedCardId));
        }
    }
}
