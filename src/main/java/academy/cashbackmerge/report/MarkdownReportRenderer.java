package academy.cashbackmerge.report;

import academy.cashbackmerge.simulation.SimulationReport;
import java.util.Map;
import java.util.TreeMap;

public final class MarkdownReportRenderer {
    private MarkdownReportRenderer() {}

    public static String render(SimulationReport report) {
        if (report == null) {
            throw new IllegalArgumentException("report не должен быть null");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("# Отчёт симуляции Cashback Merge\n\n");
        builder.append("- Игроков: ").append(report.players()).append("\n");
        builder.append("- Лимит ходов: ").append(report.movesLimit()).append("\n");
        builder.append("- Seed: ").append(report.seed()).append("\n");
        builder.append("- Стратегия: ").append(report.strategy()).append("\n\n");
        appendDistribution(
                builder,
                "Распределение максимального кэшбэка",
                "Максимальный кэшбэк",
                report.maxCashbackDistribution());

        appendDistribution(builder, "Распределение наград", "Награда", report.rewardDistribution());

        builder.append("## Score\n\n");
        builder.append("- Средний score: ").append(report.averageScore()).append("\n");
        builder.append("- Максимальный score: ").append(report.maxScore()).append("\n\n");

        builder.append("## Бизнес-ограничение\n\n");
        builder.append("- Доля игроков с максимальной наградой: ")
                .append(report.actualMaxRewardRate())
                .append("\n");
        builder.append("- Статус: ").append(report.status()).append("\n");

        return builder.toString();
    }

    private static void appendDistribution(
            StringBuilder builder, String title, String valueColumn, Map<Integer, Integer> distribution) {
        builder.append("## ").append(title).append("\n\n");
        builder.append("| ").append(valueColumn).append(" | Игроков |\n");
        builder.append("|---:|---:|\n");
        for (Map.Entry<Integer, Integer> entry : new TreeMap<>(distribution).entrySet()) {
            builder.append("| ")
                    .append(entry.getKey())
                    .append(" | ")
                    .append(entry.getValue())
                    .append(" |\n");
        }
        builder.append("\n");
    }
}
