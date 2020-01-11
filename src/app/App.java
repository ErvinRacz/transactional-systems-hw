package app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import app.assessor.Assessor;
import app.models.Operation;
import app.parser.ScheduleParser;

public class App {

    private static String readFile(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

    public static void main(String[] args) throws Exception {

        // #region read input
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

        var tMinStr = schedule.stream().min(Comparator.comparing(o -> o.getTransaction().getName())).get()
                .getTransaction().getName();
        var tMaxStr = schedule.stream().max(Comparator.comparing(o -> o.getTransaction().getName())).get()
                .getTransaction().getName();
        var scheduleInforWrapper = new Object() {
            int minT = 0;
            int maxT = 0;
        };
        try {
            scheduleInforWrapper.minT = Integer.parseInt(tMinStr);
            scheduleInforWrapper.maxT = Integer.parseInt(tMaxStr);
        } catch (NumberFormatException e) {
            System.out.println("Transaction name is not comparable! Please use integers.");
            e.printStackTrace();
        }
        // #endregion

        // var assessor = new Assessor(schedule);
        var writers = new ArrayList<BufferedWriter>();
        for (int i = scheduleInforWrapper.minT; i <= scheduleInforWrapper.maxT; i++) {
            writers.add(new BufferedWriter(new FileWriter("transaction" + i + ".txt", true)));
            // writers.get(i - 1).flush();
            writers.get(i - 1).write("SET for transaction " + i + "\n");
        }

        Function<List<Operation>, Boolean> delegate = (s) -> {
            var a = new Assessor(new ArrayList<Operation>(s));
            a.createLiveReadFromRelationList();
            for (int i = scheduleInforWrapper.minT; i <= scheduleInforWrapper.maxT; i++) {
                try {
                    writers.get(i - 1).append(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        };

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        var startTime = System.nanoTime();
        for (int i = 0; i < schedule.size(); i++) {
            PermutationProvider.swap(schedule, 0, i);

            var permutationProvider = new PermutationProvider<Operation>(true, delegate);
            // Sublist returns the subpart of the original list which remains mutable.
            // It is not thread safe until we don't pass a new copy of the list.
            permutationProvider.setElements(new LinkedList<>(schedule.subList(1, schedule.size())));
            permutationProvider.setNrOfElements(schedule.size() - 1);
            permutationProvider.setIgnoredElement(schedule.get(0));
            executor.execute(permutationProvider);

            PermutationProvider.swap(schedule, i, 0);
        }
        executor.shutdown();
        executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
        var endTime = System.nanoTime();

        // writeFile("output.txt", "Ex. time (ms): " + (endTime - startTime) /
        // 1000000f);

        writers.forEach(writer -> {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}