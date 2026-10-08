package academy.cashbackmerge.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ConfigLoader {
    private ConfigLoader() {}

    public static CampaignConfig load(Path path) throws IOException {
        JsonNode root = readJson(path);
        if (root == null || root.isMissingNode()) {
            throw new IllegalArgumentException("Файл конфигурации пуст: " + path);
        }
        if (!root.isObject()) {
            throw new IllegalArgumentException("Корень конфига должен быть JSON-объектом");
        }
        int boardSize = readRequiredInt(root, "boardSize");
        int movesLimit = readRequiredInt(root, "movesLimit");
        List<SpawnRule> spawnRules = readSpawnRules(root);

        return new CampaignConfig(boardSize, movesLimit, spawnRules);
    }

    private static JsonNode readJson(Path path) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(path.toFile());
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Некорректный JSON в файле " + path, exception);
        }
    }

    private static int readRequiredInt(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null) {
            throw new IllegalArgumentException("В конфиге отсутствует " + fieldName);
        }
        if (!node.isIntegralNumber() || !node.canConvertToInt()) {
            throw new IllegalArgumentException("Поле " + fieldName + " должно быть целым числом");
        }
        return node.intValue();
    }

    private static String readRequiredString(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null) {
            throw new IllegalArgumentException("В конфиге отсутствует " + fieldName);
        }
        if (!node.isTextual()) {
            throw new IllegalArgumentException("Поле " + fieldName + " должно быть строкой");
        }

        return node.textValue();
    }

    private static List<SpawnRule> readSpawnRules(JsonNode root) {
        JsonNode spawnRulesNode = root.get("spawnRules");
        if (spawnRulesNode == null || !spawnRulesNode.isArray()) {
            throw new IllegalArgumentException("Поле spawnRules должно быть массивом");
        }
        List<SpawnRule> spawnRules = new ArrayList<>();
        for (int index = 0; index < spawnRulesNode.size(); index++) {
            JsonNode ruleNode = spawnRulesNode.get(index);
            if (!ruleNode.isObject()) {
                throw new IllegalArgumentException("Элемент spawnRules[" + index + "] должен быть JSON-объектом");
            }

            String cashbackText = readRequiredString(ruleNode, "cashback");
            String probabilityText = readRequiredString(ruleNode, "probability");
            int cashback;
            BigDecimal probability;
            try {
                cashback = Integer.parseInt(cashbackText);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Некорректное число в поле spawnRules[" + index + "].cashback: " + cashbackText, exception);
            }
            try {
                probability = new BigDecimal(probabilityText);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Некорректное число в поле spawnRules[" + index + "].probability: " + probabilityText,
                        exception);
            }

            SpawnRule rule = new SpawnRule(cashback, probability);
            spawnRules.add(rule);
        }
        return spawnRules;
    }
}
