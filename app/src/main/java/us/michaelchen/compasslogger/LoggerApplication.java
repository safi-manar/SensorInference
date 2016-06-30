package us.michaelchen.compasslogger;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import us.michaelchen.compasslogger.utils.PreferencesWrapper;

/**
 * Created by ioreyes on 6/2/16.
 */

@ReportsCrashes(
        reportSenderFactoryClasses = {
                us.michaelchen.compasslogger.crashreporting.FirebaseSenderFactory.class
        },
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.SILENT
)

public class LoggerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PreferencesWrapper.init(this);
        ACRA.init(this);
    }
}
