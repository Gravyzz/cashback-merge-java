package academy.cashbackmerge;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.console.BoardRenderer;
import academy.cashbackmerge.game.GameFactory;
import academy.cashbackmerge.game.GameState;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Точка входа в Cashback Merge. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final Path DEFAULT_CONFIG_PATH = Path.of("config", "campaign.example.json");
    private static final long DEFAULT_SEED = 42L;

    private Main() {}

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", String.join(" ", args));

        try {
            CampaignConfig config = ConfigLoader.load(DEFAULT_CONFIG_PATH);
            GameState state = GameFactory.createInitialState(config, DEFAULT_SEED);
            System.out.println(BoardRenderer.render(state.getBoard()));
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось загрузить конфиг " + DEFAULT_CONFIG_PATH, exception);
        }
    }
}
