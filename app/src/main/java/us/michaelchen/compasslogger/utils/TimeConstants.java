package us.michaelchen.compasslogger.utils;

/**
 * Created by ioreyes on 6/8/16.
 */
public class TimeConstants {
    // Values in milliseconds
    private static long ONE_SECOND = 1000;
    private static long ONE_MINUTE = ONE_SECOND * 60;
    private static long ONE_HOUR = ONE_MINUTE * 60;
    private static long ONE_DAY = ONE_HOUR * 24;
    private static long ONE_WEEK = ONE_DAY * 7;

    // Fields used to set periodic length and uninstall time
    public static long PERIODIC_LENGTH = ONE_MINUTE;
    public static long DEADLINE_LENGTH = ONE_HOUR * 2;
    public static double PERIODIC_SAFE_FACTOR = 1.10;
}
