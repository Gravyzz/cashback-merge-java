package academy.cashbackmerge.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.console.BoardRenderer;
import academy.cashbackmerge.game.Board;
import academy.cashbackmerge.game.GameFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Загрузка конфигурации кампании")
class ConfigLoaderTest {

    @Test
    @DisplayName("Размер из JSON определяет поле и его текстовое представление")
    void jsonBoardSizeControlsInitialBoardAndRendering(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "boardSize": 5,
                  "movesLimit": 10,
                  "spawnRules": [{"cashback": "1", "probability": "1"}],
                  "rewardTable": [{"cashback": "32", "reward": "2000"}],
                  "maxRewardCashback": "32",
                  "maxRewardAllowedRate": "0.20"
                }
                """;
        Path path = Files.writeString(tempDir.resolve("five-by-five.json"), json);
        CampaignConfig config = ConfigLoader.load(path);
        Board board = GameFactory.createGame(config, 42).getState().getBoard();
        assertThat(board.getSize()).isEqualTo(5);
        assertThat(board.getEmptyPositions()).hasSize(23);
        String[] rows = BoardRenderer.render(board).split(System.lineSeparator());
        assertThat(rows).hasSize(5);
        for (String row : rows) {
            assertThat(row.split(" ")).hasSize(5);
        }
    }

    @Test
    @DisplayName("Корректный JSON-конфиг загружается из файла")
    void validConfigFileIsLoaded() throws IOException {
        Path path = Path.of("config/campaign.example.json");
        CampaignConfig config = ConfigLoader.load(path);
        assertThat(config.getBoardSize()).isEqualTo(4);
        assertThat(config.getMovesLimit()).isEqualTo(100);
        assertThat(config.getSpawnRules())
                .containsExactly(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15")));
        assertThat(config.getRewardTable())
                .containsExactly(
                        new RewardTier(2, 0),
                        new RewardTier(4, 50),
                        new RewardTier(8, 150),
                        new RewardTier(16, 500),
                        new RewardTier(32, 2000));
        assertThat(config.getMaxRewardCashback()).isEqualTo(32);
        assertThat(config.getMaxRewardAllowedRate()).isEqualByComparingTo("0.20");
    }

    @Test
    @DisplayName("Пустой файл конфигурации отклоняется с понятным сообщением")
    void emptyConfigFileIsRejected(@TempDir Path tempDir) throws IOException {
        Path path = Files.createFile(tempDir.resolve("empty.json"));
        assertThatThrownBy(() -> ConfigLoader.load(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("пуст")
                .hasMessageContaining(path.toString());
    }

    @Test
    @DisplayName("Некорректный JSON отклоняется с указанием файла и исходной причины")
    void malformedJsonIsRejectedWithFileContext(@TempDir Path tempDir) throws IOException {
        Path path = Files.writeString(tempDir.resolve("broken.json"), "{");
        assertThatThrownBy(() -> ConfigLoader.load(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(path.toString())
                .hasCauseInstanceOf(JsonProcessingException.class)
                .hasMessageContaining("Некорректный JSON");
    }

    @Test
    @DisplayName("Некорректное число cashback отклоняется с указанием поля и причины")
    void invalidCashbackNumberIsRejected(@TempDir Path tempDir) throws IOException {
        String json = """
        {
          "boardSize": 4,
          "movesLimit": 10,
          "spawnRules": [
            {"cashback": "abc", "probability": "1"}
          ]
        }
        """;
        Path path = Files.writeString(tempDir.resolve("broken.json"), json);
        assertThatThrownBy(() -> ConfigLoader.load(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasCauseInstanceOf(NumberFormatException.class)
                .hasMessageContaining("spawnRules[0].cashback")
                .hasMessageContaining("abc");
    }

    @Test
    @DisplayName("Некорректное число probability отклоняется с указанием поля и причины")
    void invalidProbabilityNumberIsRejected(@TempDir Path tempDir) throws IOException {
        String json = """
        {
          "boardSize": 4,
          "movesLimit": 10,
          "spawnRules": [
            {"cashback": "1", "probability": "abc"}
          ]
        }
        """;
        Path path = Files.writeString(tempDir.resolve("broken.json"), json);
        assertThatThrownBy(() -> ConfigLoader.load(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasCauseInstanceOf(NumberFormatException.class)
                .hasMessageContaining("spawnRules[0].probability")
                .hasMessageContaining("abc");
    }
}
