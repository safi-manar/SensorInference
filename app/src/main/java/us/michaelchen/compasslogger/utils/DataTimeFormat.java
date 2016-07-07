package us.michaelchen.compasslogger.utils;

import android.util.Log;

import java.text.ParseException;
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


    /**
     *
     * @return today's DAILY_SURVEY_DEADLINE in Milliseconds format.
     */
    public static long getDailyDeadlineInMillis() {
        // The SDF for the Date without time.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // The SDF for the Date with time formatting.
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        // Get today's date (without time) in a String format.
        Date todayDate = new Date(System.currentTimeMillis());
        String todayDateFormatted = dateFormat.format(todayDate);

        try {
            // Append the DAILY_SURVEY_DEADLINE time to the today's date.
            Date todayDeadline = format.parse( todayDateFormatted + "-" + TimeConstants.DAILY_SURVEY_DEADLINE);
            return todayDeadline.getTime();
        } catch (ParseException e) {
            Log.e("getDailyDeadline: ", e.getMessage(), e);
            return System.currentTimeMillis();
        }
    }



}
