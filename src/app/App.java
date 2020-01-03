package app;

import app.parser.*;

public class App {

    private static String s1 = "r1x r2y w1y r3z w3z r2x w2z w1x";
    private static String s2 = "r1x r2y w1y r3z w3z r2x w2z w1x";

    public static void main(String[] args) throws Exception {
        var parser = new ScheduleParser(' ');
        parser.parse(s1);
        parser.parse(s2);
    }
}