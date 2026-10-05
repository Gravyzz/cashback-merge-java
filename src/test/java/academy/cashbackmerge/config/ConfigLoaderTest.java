package academy.cashbackmerge.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Загрузка конфигурации кампании")
class ConfigLoaderTest {

    @Test
    @DisplayName("Корректный JSON-конфиг загружается из файла")
    void validConfigFileIsLoaded() throws IOException {
        Path path = Path.of("config/campaign.example.json");
        CampaignConfig config = ConfigLoader.load(path);
        assertThat(config.getBoardSize()).isEqualTo(4);
        assertThat(config.getMovesLimit()).isEqualTo(100);
        assertThat(config.getSpawnRules())
                .containsExactly(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15")));
    }
}
