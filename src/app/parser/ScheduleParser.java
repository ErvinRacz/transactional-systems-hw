package app.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import app.PermutationProvider;
import app.models.Operation;
import app.models.operands.SymbolicData;
import app.models.operands.Transaction;

/**
 * Schedule parser class which provides a parse function to process strings and
 * convert them into a schedule (List of Operations)
 * @see Operation
 */
public class ScheduleParser {

    private static final Map<String, Operation.Type> dictionary = new HashMap<>();

    static {
        dictionary.put("r", Operation.Type.READ);
        dictionary.put("w", Operation.Type.WRITE);
        dictionary.put("a", Operation.Type.ABORT);
        dictionary.put("c", Operation.Type.COMMIT);
    }

    private char delimiter;

    /**
     * Create a ScheduleParser with default delimiter, which is a space character.
     */
    public ScheduleParser() {
        super();
        delimiter = ' ';
    }

    /**
     * @param delimiter - separates the operations
     */
    public ScheduleParser(char delimiter) {
        super();
        this.setDelimiter(delimiter);
    }

    /**
     * Converts a String into a list of Operations (schedule)
     * 
     * @param schedule
     * @return
     */
    public List<Operation> parse(String schedule) {
        if (StringUtils.isBlank(schedule)) {
            throw new ScheduleParserException("The schedule must not be empty.");
        }

        List<String> operations = Arrays.asList(schedule.split(delimiter + ""));
        if (operations.size() < 2) {
            throw new ScheduleParserException("The schedule must contain at least two operations.");
        }

        if ((operations.size() - 1) != StringUtils.countMatches(schedule, delimiter)) {
            throw new ScheduleParserException("Syntax error in the user provided schedule.");
        }

        if (operations.stream().anyMatch(e -> e.length() != 3)) {
            throw new ScheduleParserException("Operation has to be constructed of three characters.");
        }

        Function<String, Operation> operationBinder = (strOp) -> new Operation(dictionary.get(strOp.substring(0, 1)),
                new SymbolicData(strOp.substring(2, 3)), new Transaction(strOp.substring(1, 2)));

        return operations.stream().map(strOp -> operationBinder.apply(strOp)).collect(Collectors.toList());
    }

    /**
     * Extracts the transactions from a schedule 
     * 
     * @param schedule
     * @return
     */
    public static List<Transaction> extractTransactions(List<Operation> schedule) {
        Set<Transaction> transactionSet = new HashSet<>();
        List<Transaction> transactionList = new ArrayList<>();
        schedule.forEach(op -> {
            if (transactionSet.add(op.getTransaction()))
                transactionList.add(op.getTransaction());
        });
        return transactionList;
    }

    public static Set<List<Operation>> getAllSerialSchedules(List<Operation> schedule) {
        var serialSchedulesSet = new HashSet<List<Operation>>();
        var serialSchedule = ScheduleParser.extractTransactions(schedule).stream()
                .map(tr -> schedule.stream().filter(op -> op.getTransaction().equals(tr)).collect(Collectors.toList()))
                .collect(Collectors.toCollection(LinkedList::new));

        Function<List<List<Operation>>, Boolean> delegate = (p) -> {
            serialSchedulesSet.add(p.stream().flatMap(List::stream).collect(Collectors.toList()));
            return true;
        };

        var permutationProvider = new PermutationProvider<List<Operation>>(false, delegate);
        permutationProvider.setElements(serialSchedule);
        permutationProvider.setNrOfElements(serialSchedule.size());
        permutationProvider.run();

        return serialSchedulesSet;
    }

    // #region Getters and Setters
    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        if (dictionary.keySet().contains(delimiter + "")) {
            throw new ScheduleParserException("The delimiter cannot be '" + delimiter + "'.");
        }
        if (StringUtils.isNumeric(delimiter + "")) {
            throw new ScheduleParserException("The delimiter cannot be a number.");
        }

        this.delimiter = delimiter;
    }
    // #endregion
}