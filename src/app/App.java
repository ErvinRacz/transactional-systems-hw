package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.List;

import app.transaction.Operation;
import app.transaction.Schedule;
import app.transaction.SymbolicData;
import app.transaction.Transaction;
import app.transaction.tgenerator.TransactionGenerator;

public class App {

    private static void writeToPosition(String filename, List<Transaction> data, long position) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(filename);

        outputStream.write(("COUNT: " + data.size() + "\r\n").getBytes());
        for (Transaction t : data) {
            byte[] strToBytes = (t.toString() + "\r\n").getBytes();
            outputStream.write(strToBytes);
        }

        outputStream.close();
    }

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
        tset1.forEach(tr -> tr.appendCommit());
        writeToPosition("N1.txt", tset1, 0);

        allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('x'));
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        allowedSymbolicDataSet.add(new SymbolicData('z'));
        var tgen2 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset2 = tgen2.generate('2', 3);
        tset2.removeIf(tr -> !tr.isCorrect());
        tset2.forEach(tr -> tr.appendCommit());
        writeToPosition("N2.txt", tset2, 0);

        allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        allowedSymbolicDataSet.add(new SymbolicData('z'));
        var tgen3 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset3 = tgen3.generate('3', 2);
        tset3.removeIf(tr -> !tr.isCorrect());
        tset3.forEach(tr -> tr.appendCommit());
        writeToPosition("N3.txt", tset3, 0);

        var counter = new Object() {
            int i = 0;
        };

        FileOutputStream outputStream = new FileOutputStream("H.txt");

        for (Transaction tA : tset1) {
            for (Transaction tB : tset2) {
                for (Transaction tC : tset3) {
                    List<Schedule> shuffleProd1 = tA.shuffleWith(tB);
                    for (Schedule sch : shuffleProd1) {
                        var xx = sch.shuffleWith(tC);
                        counter.i += xx.size();

                        byte[] strToBytes = (xx.toString() + "\r\n").getBytes();
                        outputStream.write(strToBytes);
                    }
                }
            }
        }

        outputStream.close();
        System.out.println(counter.i);
    }
}