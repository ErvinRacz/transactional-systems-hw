package app.parser;

public class ScheduleParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ScheduleParserException() {
        super();
    }

    public ScheduleParserException(String message) {
        super(message);
    }
}