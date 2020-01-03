package app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.models.Operation;
import app.parser.ScheduleParser;

public class App {

    private static String s1 = "r1x r1y w1y";
    private static String s2 = "r1x w1y w1x r3z w3z r2y r2x w2z";

    public static void main(String[] args) throws Exception {
        var parser = new ScheduleParser(' ');
        var schedule = parser.parse(s1);

        if (schedule.size() < 2) {
            throw new RuntimeException("The schedule has to contain at least two operations.");
        }

        // force serializing the schedule to see if it wasn't provided as serial
        var serialSchedule = ScheduleParser.extractTransactions(schedule).stream()
                .map(tr -> schedule.stream().filter(op -> op.getTransaction().equals(tr))).flatMap(x -> x).distinct()
                .collect(Collectors.toList());

        // PREMISE! the generated schedules must be distinct, to ensure that,
        // operations must also be distinct. This way we don't have to save schedules.
        if (!schedule.equals(serialSchedule)) {
            throw new RuntimeException("The schedule has to be provided in a serial form.");
        }

        final List<List<Operation>> generatedSchedules = new ArrayList<>();
        var permutationProvider = new PermutationProvider<Operation>((s) -> {
            generatedSchedules.add(s);
            return true;
        });
        permutationProvider.permutate(schedule.size(), schedule);

        generatedSchedules.forEach(x -> System.out.println(x));

        // printAllRecursive(serialSchedule.size(), serialSchedule.toArray());
    }
}