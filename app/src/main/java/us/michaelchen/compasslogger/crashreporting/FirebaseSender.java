package us.michaelchen.compasslogger.crashreporting;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.LinkedHashMap;
import java.util.Map;

import us.michaelchen.compasslogger.R;
import us.michaelchen.compasslogger.utils.FirebaseWrapper;

/**
 * Created by ioreyes on 6/6/16.
 */
public class FirebaseSender implements ReportSender {
    private static final String CRASH_KEY = "crashes";

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        if(FirebaseWrapper.isInit()) {
            Map<String, Object> data = new LinkedHashMap<>();
            for(ReportField rf : errorContent.keySet()) {
                data.put(rf.name(), errorContent.getProperty(rf).toString());
            }

            // Push error data to backend
            FirebaseWrapper.push(CRASH_KEY, data);
        } else {
            throw new ReportSenderException(context.getString(R.string.error_not_init));
        }
    }
}
