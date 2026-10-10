package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameFactoryTest {

    @Test
    @DisplayName("Фабрика использует размер из конфига и размещает ровно две настроенные плитки")
    void initialBoardUsesConfiguredSize() {
        for (int size : new int[] {2, 3, 5}) {
            CampaignConfig config = new CampaignConfig(size, 7, List.of(new SpawnRule(2, BigDecimal.ONE)));
            GameState state = GameFactory.createGame(config, 42).getState();
            Board board = state.getBoard();
            assertThat(board.getSize()).isEqualTo(size);
            assertThat(board.getEmptyPositions()).hasSize(size * size - 2);
            int totalCashback = 0;
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    assertThat(board.get(row, column)).isIn(0, 2);
                    totalCashback += board.get(row, column);
                }
            }
            assertThat(totalCashback).isEqualTo(4);
            assertThat(state.getMovesUsed()).isZero();
            assertThat(state.getMovesRemaining()).isEqualTo(7);
            assertThat(state.isFinished()).isFalse();
        }
    }

    @Test
    @DisplayName("Начальное состояние содержит две плитки и настроенный лимит ходов")
    void initialStateContainsTwoTilesAndConfiguredMoveLimit() {
        CampaignConfig config = new CampaignConfig(4, 10, List.of(new SpawnRule(1, BigDecimal.ONE)));
        GameState gameState = GameFactory.createGame(config, 42).getState();
        assertThat(gameState.getBoard().getEmptyPositions()).hasSize(14);
        assertThat(gameState.getMovesUsed()).isZero();
        assertThat(gameState.getMovesRemaining()).isEqualTo(10);
        assertThat(gameState.isFinished()).isFalse();
    }

    @Test
    @DisplayName("Созданная фабрикой игра выполняет ход и обновляет своё состояние")
    void createdGameAppliesMoveToItsState() {
        CampaignConfig config = new CampaignConfig(3, 5, List.of(new SpawnRule(1, BigDecimal.ONE)));
        Game game = GameFactory.createGame(config, 42);
        GameState state = game.getState();
        Board board = state.getBoard();
        // Задаём известное поле, чтобы проверять связь компонентов, а не случайное расположение.
        board.replaceCells(new int[][] {{1, 1, 0}, {0, 0, 0}, {0, 0, 0}});

        MoveResult result = game.applyMove(Direction.LEFT);

        assertThat(game.getState()).isSameAs(state);
        assertThat(result.changed()).isTrue();
        assertThat(result.scoreGain()).isEqualTo(2);
        assertThat(state.getScore()).isEqualTo(2);
        assertThat(state.getMovesUsed()).isEqualTo(1);
        assertThat(state.getMovesRemaining()).isEqualTo(4);
        assertThat(board.get(0, 0)).isEqualTo(2);
        assertThat(board.getEmptyPositions()).hasSize(7);
    }

    @Test
    @DisplayName("Начальное состояние нельзя создать без конфига")
    void nullConfigIsRejected() {
        CampaignConfig config = null;
        assertThatThrownBy(() -> GameFactory.createGame(config, 42)).isInstanceOf(IllegalArgumentException.class);
    }
}
