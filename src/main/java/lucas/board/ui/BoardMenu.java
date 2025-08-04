package lucas.board.ui;

import lombok.AllArgsConstructor;
import lucas.board.persistence.entity.BoardEntity;
import lucas.board.service.BoardQueryService;

import java.util.Scanner;

import static lucas.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {
    private BoardEntity boardEntity;

    private final Scanner scanner = new Scanner(System.in);

    public void execute() {
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


    private void showColumn() {
    }

    private void showCard() {
    }
}
