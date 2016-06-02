package us.michaelchen.compasslogger.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ioreyes on 6/2/16.
 */
public class DataTimeFormat {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss(zzz)");

    /**
     * Formats the time as yyyy-MM-dd-HH:mm:ss(zzz) as specified by DataTimeFormat.FORMAT
     * @param d Date to format
     * @return time as yyyy-MM-dd-HH:mm:ss(zzz)
     */
    public static String format(Date d) {
        return FORMAT.format(d);
    }

    /**
     * Formats the time as yyyy-MM-dd-HH:mm:ss(zzz) as specified by DataTimeFormat.FORMAT
     * @param l UTC time from Unix epoch in milliseconds
     * @return time as yyyy-MM-dd-HH:mm:ss(zzz)
     */
    public static String format(long l) {
        return format(new Date(l));
    }

    /**
     *
     * @return current time as yyyy-MM-dd-HH:mm:ss(zzz)
     */
    public static String current() {
        return format(System.currentTimeMillis());
    }
}
