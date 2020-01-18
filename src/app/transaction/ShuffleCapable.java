package app.transaction;

import java.util.List;

public interface ShuffleCapable {
    List<Schedule> shuffleWith(ShuffleCapable shuffleCapable);
    List<Operation> getOperations();
}