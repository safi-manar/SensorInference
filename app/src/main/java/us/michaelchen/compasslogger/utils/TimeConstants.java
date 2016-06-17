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
    public static long PERIODIC_SAFE_INTERVAL = (long) (PERIODIC_LENGTH * PERIODIC_SAFE_FACTOR);

    // The maximum amount of time to keep a sensor open without an onSensorChanged event
    public static long MAX_SENSOR_TIME = ONE_SECOND * 3;
    public static long SENSOR_DATA_POLL_INTERVAL = 200; // milliseconds, corresponds to SENSOR_DELAY_NORMAL
}
