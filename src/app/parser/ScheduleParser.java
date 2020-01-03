package app.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import app.models.*;

public class ScheduleParser {

    private static final Map<String, Operation.Type> dictionary = new HashMap<>();

    static {
        dictionary.put("r", Operation.Type.READ);
        dictionary.put("w", Operation.Type.WRITE);
        dictionary.put("a", Operation.Type.ABORT);
        dictionary.put("c", Operation.Type.COMMIT);
    }

    private char delimiter;

    public ScheduleParser() {
        super();
    }

    public ScheduleParser(char delimiter) {
        super();
        this.setDelimiter(delimiter);
    }

    public List<Operation> parse(final String schedule) throws ScheduleParserException {
        List<String> operations = Arrays.asList(schedule.split(delimiter + ""));

        if ((operations.size() - 1) != StringUtils.countMatches(schedule, delimiter))
            throw new ScheduleParserException("Syntax error in the user provided schedule.");

        if (operations.size() < 2)
            throw new ScheduleParserException("The schedule must contain at least two operations.");

        return null;
    }

    // #region Getters and Setters
    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }
    // #endregion
}