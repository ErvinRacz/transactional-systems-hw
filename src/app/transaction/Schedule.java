package app.transaction;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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