package academy.cashbackmerge.simulation;

import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.GameState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RandomMoveStrategy implements MoveStrategy {
    private final Random random;

    public RandomMoveStrategy(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random должен быть не null");
        }
        this.random = random;
    }

    @Override
    public List<Direction> getMoveOrder(GameState state) {
        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        Collections.shuffle(directions, random);
        return directions;
    }
}
