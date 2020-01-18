package app.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Transaction extends Operand {
    private static final long serialVersionUID = 1L;

    private List<Operation> operations;

    public Transaction() {
        super();
    }

    public Transaction(char name) {
        super(Operand.Type.TRANSACTION, name);
    }

    public Transaction(char name, List<Operation> operations) {
        this(name);
        this.operations = operations;
    }

    public boolean isCorrect() {
        if (operations == null || operations.size() < 2) {
            return false;
        }
        Map<String, Integer> opCounterPerOperand = new HashMap<>();
        operations.forEach(op -> opCounterPerOperand.putIfAbsent(op.toString(), 0));

        for (int i = operations.size() - 1; i >= 0; i--) {
            var count = opCounterPerOperand.get(operations.get(i).toString());
            opCounterPerOperand.put(operations.get(i).toString(), count++);
            if (count > 1) {
                return false;
            }

            if (operations.get(i).getType().equals(Operation.Type.READ)) {
                for (int j = i - 1; j >= 0; j--) {
                    if (operations.get(j).getType().equals(Operation.Type.WRITE)
                            && operations.get(i).getOperand().equals(operations.get(j).getOperand())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<Operation> getOperations() {
        if (operations == null) {
            operations = new ArrayList<Operation>();
        }
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}