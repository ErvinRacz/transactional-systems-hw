package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import app.transaction.Assessor;
import app.transaction.Operation;
import app.transaction.Schedule;
import app.transaction.SymbolicData;
import app.transaction.Transaction;
import app.transaction.aspects.CSR;
import app.transaction.aspects.FSR;
import app.transaction.aspects.VSR;
import app.transaction.tgenerator.TransactionGenerator;

public class App {

    private static void writeToPosition(String filename, List<Transaction> data) throws IOException {
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
        writeToPosition("N1.txt", tset1);

        allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('x'));
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        allowedSymbolicDataSet.add(new SymbolicData('z'));
        var tgen2 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset2 = tgen2.generate('2', 3);
        tset2.removeIf(tr -> !tr.isCorrect());
        tset2.forEach(tr -> tr.appendCommit());
        writeToPosition("N2.txt", tset2);

        allowedSymbolicDataSet = new HashSet<SymbolicData>();
        allowedSymbolicDataSet.add(new SymbolicData('y'));
        allowedSymbolicDataSet.add(new SymbolicData('z'));
        var tgen3 = new TransactionGenerator(allowedSymbolicDataSet, allowedOperationSet);
        var tset3 = tgen3.generate('3', 2);
        tset3.removeIf(tr -> !tr.isCorrect());
        tset3.forEach(tr -> tr.appendCommit());
        writeToPosition("N3.txt", tset3);

        var counter = new Object() {
            int H = 0;
            int FSR = 0;
            int CSR = 0;
        };

        FileOutputStream outputStreamH = new FileOutputStream("H.txt");
        FileOutputStream outputStreamFSR = new FileOutputStream("FSR.txt");
        FileOutputStream outputStreamCSR = new FileOutputStream("CSR.txt");

        for (Transaction tA : tset1) {
            for (Transaction tB : tset2) {
                for (Transaction tC : tset3) {
                    var first = true;
                    Set<Schedule> serials;
                    final List<Assessor> serialSchedulesAssessors = new ArrayList<Assessor>();

                    List<Schedule> shuffleProd1 = tA.shuffleWith(tB);
                    for (Schedule schProd1 : shuffleProd1) {
                        var scheduleBatch = schProd1.shuffleWith(tC);
                        counter.H += scheduleBatch.size();

                        if (first) {
                            serials = scheduleBatch.get(0).getAllSerialSchedules();
                            // Acquire every variation of the original serial schedule
                            serialSchedulesAssessors
                                    .addAll(serials.stream().map(s -> new Assessor(s)).collect(Collectors.toList()));
                            serialSchedulesAssessors.forEach(a -> {
                                a.createLiveReadFromRelationList();
                                a.createReadFromRelationList();
                            });
                            first = false;
                        }

                        for (Schedule sch : scheduleBatch) {
                            var a = new Assessor(sch);
                            a.createReadFromRelationList();
                            a.createLiveReadFromRelationList();
                            // Check the final state serializability of the current permutattion
                            if (a.verifies(new FSR(serialSchedulesAssessors))) {
                                counter.FSR++;
                                outputStreamFSR.write(toBytesWithNL(sch.toString()));
                            }
                            if (a.verifies(new VSR(serialSchedulesAssessors)) && a.verifies(new CSR())) {
                                counter.CSR++;
                                outputStreamCSR.write(toBytesWithNL(sch.toString()));
                            }
                            outputStreamH.write(toBytesWithNL(sch.toString()));
                        }

                        System.out.println(counter.H);
                    }
                }
            }
        }

        outputStreamH.close();
        outputStreamFSR.close();
        outputStreamCSR.close();
        System.out.println(counter.H + " # " + counter.FSR + " # " + counter.CSR);
    }

    public static byte[] toBytesWithNL(String str) {
        return (str.toString() + "\r\n").getBytes();
    }
}