package app.transaction.tgenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import app.transaction.Operation;
import app.transaction.SymbolicData;
import app.transaction.Transaction;

public class TransactionGenerator {
    private final Set<Operation.Type> operationTypeSet = new HashSet<>();
    private final Set<SymbolicData> symbolicDataSet = new HashSet<>();
    private List<Operation> extendedOperations;
    private boolean baseSetsPossiblyModified = false;

    public TransactionGenerator() {
    }

    /**
     * @param allowedSymbolicDataSet - set of symbols
     * @param allowedOperationsSet   - set of allowed operations (e.g. read, write)
     */
    public TransactionGenerator(Set<SymbolicData> allowedSymbolicDataSet, Set<Operation.Type> allowedOperationsSet) {
        this.operationTypeSet.addAll(allowedOperationsSet);
        this.symbolicDataSet.addAll(allowedSymbolicDataSet);
        extendOperations();
    }

    private void extendOperations() {
        extendedOperations = new ArrayList<>();
        symbolicDataSet.forEach(dataSym -> {
            operationTypeSet.forEach(opType -> {
                extendedOperations.add(new Operation(opType, dataSym));
            });
        });
        baseSetsPossiblyModified = false;
    }

    /**
     * @param transactionName
     * @param combinationOf   - nr of operations in a transaction
     * @return
     */
    public List<Transaction> generate(char transactionName, int opNr) {
        if (operationTypeSet.isEmpty() || symbolicDataSet.isEmpty())
            throw new RuntimeException("operationSet and symbolicDataSet should not be empty!");
        if (baseSetsPossiblyModified)
            extendOperations();

        List<List<Operation>> combinationLists = new LinkedList<>();
        for (int i = 0; i < opNr; i++) {
            var ops = new LinkedList<Operation>();
            extendedOperations.forEach(op -> ops.add(Operation.copyOf(op)));
            combinationLists.add(ops);
        }

        var transactionVariants = computeCombinations(combinationLists);
        var ret = new ArrayList<Transaction>();
        transactionVariants.forEach(trAsList -> {
            var transaction = new Transaction(transactionName);
            transaction.getOperations().addAll(trAsList.stream().map(op -> {
                var newOp = Operation.copyOf(op);
                newOp.setTransaction(transaction);
                return newOp;
            }).collect(Collectors.toList()));
            ret.add(transaction);
        });

        return ret;
    }

    public static <T> List<List<T>> appendElements(List<List<T>> combinations, List<T> extraElements) {
        return combinations.stream().flatMap(oldCombination -> extraElements.stream().map(extra -> {
            List<T> combinationWithExtra = new ArrayList<>(oldCombination);
            combinationWithExtra.add(extra);
            return combinationWithExtra;
        })).collect(Collectors.toList());
    }

    public static <T> List<List<T>> computeCombinations(List<List<T>> lists) {
        List<List<T>> currentCombinations = Arrays.asList(Arrays.asList());
        for (List<T> list : lists) {
            currentCombinations = appendElements(currentCombinations, list);
        }
        return currentCombinations;
    }

    // #region Helpers

    public Set<Operation.Type> getOperationTypeSet() {
        baseSetsPossiblyModified = true;
        return this.operationTypeSet;
    }

    public Set<SymbolicData> getSymbolicDataSet() {
        baseSetsPossiblyModified = true;
        return this.symbolicDataSet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransactionGenerator)) {
            return false;
        }
        TransactionGenerator transactionGenerator = (TransactionGenerator) o;
        return Objects.equals(operationTypeSet, transactionGenerator.operationTypeSet)
                && Objects.equals(symbolicDataSet, transactionGenerator.symbolicDataSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationTypeSet, symbolicDataSet);
    }

    // #endregion
}