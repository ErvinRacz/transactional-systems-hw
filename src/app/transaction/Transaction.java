package app.transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Transaction extends Operand implements ShuffleCapable {
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

    /**
     * In each transaction each data item is must be read or written at most once
     * and no data item is read (again) after it has been written.
     * 
     * @return
     */
    public boolean isCorrect() {
        if (operations == null || operations.size() < 2) {
            return false;
        }
        Set<String> opMonitor = new HashSet<>();

        for (int i = operations.size() - 1; i >= 0; i--) {
            if (!opMonitor.add(operations.get(i).toString())) {
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

    public void appendCommit() {
        if (!operations.get(operations.size() - 1).getType().equals(Operation.Type.COMMIT)) {
            operations.add(new Operation(Operation.Type.COMMIT, this, this));
        }
    }

    @Override
    public List<Schedule> shuffleWith(ShuffleCapable shuffleCapable) {
        return ShuffleUtils.shuffleProduct(this.operations, shuffleCapable.getOperations()).stream()
                .map(ops -> new Schedule(ops)).collect(Collectors.toList());
    }

    @Override
    public List<Operation> getOperations() {
        if (operations == null) {
            operations = new ArrayList<Operation>();
        }
        return operations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Operation operation : operations) {
            sb.append(operation.toString() + " ");
        }
        return "{ " + sb.toString() + "}";
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}