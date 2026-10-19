package academy.cashbackmerge.simulation;

import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.GameState;
import java.util.List;

public interface MoveStrategy {
    List<Direction> getMoveOrder(GameState state);
}
