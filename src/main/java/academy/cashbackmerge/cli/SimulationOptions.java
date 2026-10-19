package academy.cashbackmerge.cli;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record SimulationOptions(Path configPath, int players, String strategy, long seed, Path outputPath) {
    private static final String SIMULATE_COMMAND = "simulate";
    private static final String RANDOM_STRATEGY = "random";
    private static final Set<String> SUPPORTED_OPTIONS =
            Set.of("--config", "--players", "--strategy", "--seed", "--output");

    public static SimulationOptions parse(String[] args) {
        if (args == null || args.length == 0 || !SIMULATE_COMMAND.equals(args[0])) {
            throw new IllegalArgumentException(usage());
        }

        Map<String, String> values = new HashMap<>();
        for (int index = 1; index < args.length; index += 2) {
            if (index + 1 >= args.length) {
                throw new IllegalArgumentException("Не указано значение для аргумента " + args[index]);
            }

            String option = args[index];
            if (!SUPPORTED_OPTIONS.contains(option)) {
                throw new IllegalArgumentException("Неизвестный аргумент: " + option);
            }
            if (values.putIfAbsent(option, args[index + 1]) != null) {
                throw new IllegalArgumentException("Аргумент указан повторно: " + option);
            }
        }

        Path configPath = Path.of(requiredValue(values, "--config"));
        int players = parsePlayers(requiredValue(values, "--players"));
        String strategy = requiredValue(values, "--strategy");
        long seed = parseSeed(requiredValue(values, "--seed"));
        Path outputPath = Path.of(requiredValue(values, "--output"));

        if (!RANDOM_STRATEGY.equals(strategy)) {
            throw new IllegalArgumentException("Поддерживается только стратегия random");
        }

        return new SimulationOptions(configPath, players, strategy, seed, outputPath);
    }

    private static String requiredValue(Map<String, String> values, String option) {
        String value = values.get(option);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Не указан обязательный аргумент " + option);
        }
        return value;
    }

    private static int parsePlayers(String text) {
        try {
            int players = Integer.parseInt(text);
            if (players <= 0) {
                throw new IllegalArgumentException("Число игроков должно быть положительным");
            }
            return players;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Некорректное число игроков: " + text, exception);
        }
    }

    private static long parseSeed(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Некорректный seed: " + text, exception);
        }
    }

    public static String usage() {
        return "Использование: simulate --config <path> --players <number> "
                + "--strategy random --seed <number> --output <path>";
    }
}
