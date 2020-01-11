package app;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import app.assessor.Assessor;
import app.assessor.aspects.CSR;
import app.assessor.aspects.FSR;
import app.assessor.aspects.VSR;
import app.models.Operation;
import app.parser.ScheduleParser;

public class App {

    private static FileOutputStream fosH, fosCSR;
    private static DataOutputStream fH, fCSR;

    private static String readFile(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

    private static void writeTo(DataOutputStream output, String str) {
        try {
            output.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        fosH = new FileOutputStream("H.txt");
        fosCSR = new FileOutputStream("CSR.txt");
        fH = new DataOutputStream(new BufferedOutputStream(fosH));
        fCSR = new DataOutputStream(new BufferedOutputStream(fosCSR));

        // #region Read input
        String readSerialSchedule = "";
        try {
            readSerialSchedule = readFile("input.txt");
        } catch (IOException e) {
            System.out.println("Input file cannot be found\n");
            e.printStackTrace();
        }

        var parser = new ScheduleParser();
        var schedule = parser.parse(readSerialSchedule);

        if (schedule.size() < 2) {
            throw new RuntimeException("The schedule has to contain at least two operations.");
        }

        // force serializing the schedule to see if it wasn't provided as serial
        var serialSchedule = ScheduleParser.extractTransactions(schedule).stream()
                .map(tr -> schedule.stream().filter(op -> op.getTransaction().equals(tr))).flatMap(x -> x).distinct()
                .collect(Collectors.toList());

        // PREMISE! the generated schedules must be distinct. This way we don't have to
        // save schedules and we can process them parallelly
        if (!schedule.equals(serialSchedule)) {
            throw new RuntimeException("The schedule has to be provided in a serial form.");
        }

        var serialSchedules = ScheduleParser.getAllSerialSchedules(schedule);

        // #endregion
        var serialSchedulesAssessors = serialSchedules.stream().map(s -> new Assessor(new ArrayList<Operation>(s)))
                .collect(Collectors.toList());

        serialSchedulesAssessors.forEach(a -> {
            a.createLiveReadFromRelationList();
            a.createReadFromRelationList();
        });

        Function<List<Operation>, Boolean> delegate = (s) -> {
            var a = new Assessor(new ArrayList<Operation>(s));
            a.createReadFromRelationList();
            a.createLiveReadFromRelationList();
            if (a.verifies(new FSR(assessor.getSchedule(), assessor.getLiveReadFromRealations()))) {
                writeTo(fH, s.toString() + " - LRF: " + a.getLiveReadFromRealations().toString() + "\n");
            }
            var csr = new CSR();
            if (a.verifies(new VSR(assessor.getSchedule(), assessor.getLiveReadFromRealations())) && a.verifies(csr)) {
                writeTo(fCSR, s.toString() + " - Conflict graph: " + csr.getConflictGraph().toString() + "\n");
            }
            return true;
        };

        // ThreadPoolExecutor executor = (ThreadPoolExecutor)
        // Executors.newFixedThreadPool(20);
        var startTime = System.nanoTime();
        for (int i = 0; i < schedule.size(); i++) {
            PermutationProvider.swap(schedule, 0, i);

            var permutationProvider = new PermutationProvider<Operation>(true, delegate);
            // Sublist returns the subpart of the original list which remains mutable.
            // It is not thread safe until we don't pass a new copy of the list.
            permutationProvider.setElements(new LinkedList<>(schedule.subList(1, schedule.size())));
            permutationProvider.setNrOfElements(schedule.size() - 1);
            permutationProvider.setIgnoredElement(schedule.get(0));
            permutationProvider.run();
            // executor.execute(permutationProvider);

            PermutationProvider.swap(schedule, i, 0);
        }
        // executor.shutdown();
        // executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
        var endTime = System.nanoTime();
        System.out.println("time (ms): " + (endTime - startTime) / 1000000f);

        fH.close();
        fCSR.close();
    }
}