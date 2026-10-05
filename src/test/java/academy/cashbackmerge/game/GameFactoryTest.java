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
    @DisplayName("Начальное состояние содержит две плитки и настроенный лимит ходов")
    void initialStateContainsTwoTilesAndConfiguredMoveLimit() {
        CampaignConfig config = new CampaignConfig(4, 10, List.of(new SpawnRule(1, BigDecimal.ONE)));
        GameState gameState = GameFactory.createInitialState(config, 42);
        assertThat(gameState.getBoard().getEmptyPositions()).hasSize(14);
        assertThat(gameState.getMovesUsed()).isZero();
        assertThat(gameState.getMovesRemaining()).isEqualTo(10);
        assertThat(gameState.isFinished()).isFalse();
    }

    @Test
    @DisplayName("Начальное состояние нельзя создать без конфига")
    void nullConfigIsRejected() {
        CampaignConfig config = null;
        assertThatThrownBy(() -> GameFactory.createInitialState(config, 42))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
