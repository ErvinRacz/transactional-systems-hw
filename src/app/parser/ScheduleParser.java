package app.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import app.models.Operation;
import app.models.operands.SymbolicData;
import app.models.operands.Transaction;

/**
 * Schedule parser class which provides a parse function to process strings that
 * meet the follosing conditions: <br/>
 * - operations have to be separated by one character which is called delimiter
 * <br/>
 * - delimiter can be any kind of character that is not used in the notation of
 * an operation <br/>
 * - schedule must contain at least two operations <br/>
 * - the notation of an operation is composed of three caharacters: <br/>
 * 1. type of operation (r, w, a or c - meaning read, write, abort and commit)
 * <br/>
 * 2. transaction that the operation is being part of (must be a number) <br/>
 * 3. operand which has to be a character (either denoting the subjected data or
 * the transaction)
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
     * TODO: provide description
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
     * Defines a set of transactions
     * 
     * @param schedule - list of operations
     * @return - Sets don't ensure consistent ordering of the elements, thus we need
     *         to return a list
     */
    public static List<Transaction> extractTransactions(Collection<? extends Operation> schedule) {
        Set<Transaction> transactionSet = new HashSet<>();
        List<Transaction> transactionList = new ArrayList<>();
        schedule.forEach(op -> {
            if (transactionSet.add(op.getTransaction()))
                transactionList.add(op.getTransaction());
        });
        return transactionList;
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