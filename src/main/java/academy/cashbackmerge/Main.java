package academy.cashbackmerge;

import academy.cashbackmerge.cli.SimulationOptions;
import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.report.MarkdownReportRenderer;
import academy.cashbackmerge.simulation.BatchSimulator;
import academy.cashbackmerge.simulation.GameSimulationResult;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.SimulationReportCalculator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Точка входа в Cashback Merge. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {}

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", args == null ? "null" : String.join(" ", args));
        if (args == null || args.length == 0) {
            System.out.println(SimulationOptions.usage());
            return;
        }

        try {
            SimulationOptions options = SimulationOptions.parse(args);
            CampaignConfig config = ConfigLoader.load(options.configPath());
            List<GameSimulationResult> results =
                    BatchSimulator.simulateAllGames(config, options.players(), options.seed());
            SimulationReport report =
                    SimulationReportCalculator.calculate(results, config, options.seed(), options.strategy());
            String markdown = MarkdownReportRenderer.render(report);

            Path outputPath = options.outputPath().toAbsolutePath();
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, markdown, StandardCharsets.UTF_8);
            System.out.println("Отчёт сохранён: " + outputPath);
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось прочитать конфиг или сохранить отчёт", exception);
        }
    }
}
