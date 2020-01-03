package app;

import java.util.List;
import java.util.stream.Collectors;

import app.models.Operation;
import app.parser.*;

public class App {

    private static String s1 = "r1x r2y w1y r3z w3z r2x w2z w1x";
    private static String s2 = "r1x w1y w1x r3z w3z r2y r2x w2z";

    public static void main(String[] args) throws Exception {
        var parser = new ScheduleParser(' ');
        var schedule = parser.parse(s2);

        if (schedule.size() < 2) {
            throw new RuntimeException("The schedule has to contain at least two operations.");
        }

        var serialSchedule = ScheduleParser.extractTransactions(schedule).stream()
                .map(tr -> schedule.stream().filter(op -> op.getTransaction().equals(tr))).flatMap(x -> x).distinct()
                .collect(Collectors.toList());

        if (!schedule.equals(serialSchedule)) {
            throw new RuntimeException("The schedule has to be provided in a serial form.");
        }
        
        System.out.println("");
        // while(var permutation = permutate(schedule)){

        // }
    }

    private static List<Operation> permutate(List<Operation> schedule) {
        return null;
    }
}