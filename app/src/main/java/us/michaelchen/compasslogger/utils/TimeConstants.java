package us.michaelchen.compasslogger.utils;

/**
 * Created by ioreyes on 6/8/16.
 */
public class TimeConstants {
    // Values in milliseconds
    private static long ONE_SECOND = 1000;
    private static long ONE_MINUTE = ONE_SECOND * 60;
    public static long ONE_HOUR = ONE_MINUTE * 60;
    public static long ONE_DAY = ONE_HOUR * 24;
    private static long ONE_WEEK = ONE_DAY * 7;

    // Sensor reporting period
    public static long PERIODIC_LENGTH = ONE_MINUTE * 1;
    public static long LOCATION_PERIODIC_LENGTH = PERIODIC_LENGTH;

    // Periodic active/inactive status checking
    public static double PERIODIC_SAFE_FACTOR = 1.10;
    public static long PERIODIC_SAFE_INTERVAL = (long) (PERIODIC_LENGTH * PERIODIC_SAFE_FACTOR);

    // App lifespan until uninstallation
    public static long DEADLINE_LENGTH = ONE_DAY * 2;

    // The scheduling time for daily surveys in HH:mm:ss format.
    public static String DAILY_SURVEY_DEADLINE = "20:00:00";
    public static long DAILY_SURVEY_WINDOW = ONE_HOUR * 4;
    public static long DAILY_SURVEY_POSTPONEMENT = ONE_HOUR;

    // The maximum amount of time to keep a sensor open without an onSensorChanged event
    public static long MAX_SENSOR_TIME = ONE_SECOND * 3;
    public static long SENSOR_DATA_POLL_INTERVAL = 200; // milliseconds, corresponds to SENSOR_DELAY_NORMAL

    // Used to determine epoch and boot timestamps
    public static long FORTY_YEARS_MS = ONE_DAY * 365 * 40;    // It's been at least 40 years since the Unix epoch
    public static long FORTY_YEARS_NS = FORTY_YEARS_MS * (long)1e6;
}
