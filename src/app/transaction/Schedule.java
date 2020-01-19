package app.transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import app.PermutationProvider;

public class Schedule implements ShuffleCapable {

    private List<Operation> operations;

    public Schedule() {
    }

    public Schedule(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public List<Schedule> shuffleWith(ShuffleCapable shuffleCapable) {
        return ShuffleUtils.shuffleProduct(this.operations, shuffleCapable.getOperations()).stream()
                .map(ops -> new Schedule(ops)).collect(Collectors.toList());
    }

    @Override
    public List<Operation> getOperations() {
        return this.operations;
    }

    /**
     * Extracts the transactions from a schedule
     * 
     * @param schedule
     * @return
     */
    public List<Transaction> extractTransactions() {
        Set<Transaction> transactionSet = new HashSet<>();
        List<Transaction> transactionList = new ArrayList<>();
        this.operations.forEach(op -> {
            if (transactionSet.add(op.getTransaction()))
                transactionList.add(op.getTransaction());
        });
        return transactionList;
    }

    public Set<Schedule> getAllSerialSchedules() {
        var serialSchedule = extractTransactions();
        var serialSchedulesSet = new HashSet<Schedule>();

        Function<List<Transaction>, Boolean> delegate = (p) -> {
            var sch = new Schedule(new ArrayList<Operation>());
            p.forEach(tr -> sch.getOperations().addAll(tr.getOperations()));
            serialSchedulesSet.add(sch);
            return true;
        };

        var permutationProvider = new PermutationProvider<Transaction>(delegate);
        permutationProvider.setElements(serialSchedule);
        permutationProvider.setNrOfElements(serialSchedule.size());
        permutationProvider.run();

        return serialSchedulesSet;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public Schedule operations(List<Operation> operations) {
        this.operations = operations;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Schedule)) {
            return false;
        }
        Schedule schedule = (Schedule) o;
        return Objects.equals(operations, schedule.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(operations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Operation operation : operations) {
            sb.append(operation.toString() + " ");
        }
        return "{ " + sb.toString() + "}";
    }
}