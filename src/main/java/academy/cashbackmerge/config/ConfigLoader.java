package academy.cashbackmerge.config;

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
        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("Корень конфига должен быть JSON-объектом");
        }
        int boardSize = readRequiredInt(root, "boardSize");
        int movesLimit = readRequiredInt(root, "movesLimit");
        List<SpawnRule> spawnRules = readSpawnRules(root);

        return new CampaignConfig(boardSize, movesLimit, spawnRules);
    }

    private static JsonNode readJson(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(path.toFile());
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
        for (JsonNode ruleNode : spawnRulesNode) {
            if (!ruleNode.isObject()) {
                throw new IllegalArgumentException("Элемент spawnRules должен быть JSON-объектом");
            }

            String cashbackText = readRequiredString(ruleNode, "cashback");
            String probabilityText = readRequiredString(ruleNode, "probability");

            int cashback = Integer.parseInt(cashbackText);
            BigDecimal probability = new BigDecimal(probabilityText);

            SpawnRule rule = new SpawnRule(cashback, probability);
            spawnRules.add(rule);
        }
        return spawnRules;
    }
}
