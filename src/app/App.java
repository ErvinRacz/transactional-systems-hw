package app;

import java.util.HashSet;

import app.transaction.Operation;
import app.transaction.SymbolicData;
import app.transaction.tgenerator.TransactionGenerator;

public class App {

    public static void main(String[] args) throws Exception {
        var allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('x'));
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        var allowedOperationSet = new HashSet<Operation.Type>();
        allowedOperationSet.add(Operation.Type.READ);
        allowedOperationSet.add(Operation.Type.WRITE);
        var tgen1 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset1 = tgen1.generate('1', 2);
        tset1.removeIf(tr -> !tr.isCorrect());

        allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('x'));
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        allowedSymbolicDataSet.add(new SymbolicData('z'));
        var tgen2 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset2 = tgen2.generate('2', 3);
        tset2.removeIf(tr -> !tr.isCorrect());

        allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        allowedSymbolicDataSet.add(new SymbolicData('z'));
        var tgen3 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset3 = tgen3.generate('3', 2);
        tset3.removeIf(tr -> !tr.isCorrect());

        System.out.println("x");
    }
}