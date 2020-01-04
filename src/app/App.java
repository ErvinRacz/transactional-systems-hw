package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import app.models.Operation;
import app.assessor.*;
import app.parser.ScheduleParser;

public class App {

    private static String s2 = "r1x w1y w1x r3z w3z r2y r2x w2z";

    public static void main(String[] args) throws Exception {
        var parser = new ScheduleParser(' ');
        var schedule = parser.parse(s2);

        if (schedule.size() < 2) {
            throw new RuntimeException("The schedule has to contain at least two operations.");
        }

        // force serializing the schedule to see if it wasn't provided as serial
        var serialSchedule = ScheduleParser.extractTransactions(schedule).stream()
                .map(tr -> schedule.stream().filter(op -> op.getTransaction().equals(tr))).flatMap(x -> x).distinct()
                .collect(Collectors.toList());

        // PREMISE! the generated schedules must be distinct. To ensure that,
        // operations must also be distinct. This way we don't have to save schedules
        // and we can process them parallelly
        if (!schedule.equals(serialSchedule)) {
            throw new RuntimeException("The schedule has to be provided in a serial form.");
        }

        final List<List<Operation>> generatedSchedules = new ArrayList<>();
        Function<List<Operation>, Boolean> delegate = (s) -> {
            var assessor = new Assessor(s);
            System.out.println(assessor.getStepGraph().toString());
            return true;
        };

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

        var startTime = System.nanoTime();
        for (int i = 0; i < schedule.size(); i++) {
            PermutationProvider.swap(schedule, 0, i);

            var permutationProvider = new PermutationProvider<Operation>(true, delegate);
            permutationProvider.setElements(schedule.subList(1, schedule.size()));
            permutationProvider.setNrOfElements(schedule.size() - 1);
            permutationProvider.setIgnoredElement(schedule.get(0));

            executor.execute(permutationProvider);

            PermutationProvider.swap(schedule, i, 0);
        }
        executor.shutdown();
        executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
        var endTime = System.nanoTime();

        System.out.println("Ex. time (ms): " + (endTime - startTime) / 1000000f + "\nNr. of schedules: "
                + generatedSchedules.size());
    }
}