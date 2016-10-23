package edu.berkeley.icsi.sensormonitor.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manar on 10/22/2016.
 * A supplementary FirebaseWrapper to log activity on user devices.
 */
public class FBLogger {

    // Key
    private static final String LOGS = "logs";

    // String labels.
    private static final String CONSENT = "consent";
    private static final String DAILYSURVEY = "dailysurvey";
    private static final String DEADLINESURVEY = "deadlinesurvey";



    public static void consentActivity() {
        Map<String, Object> data = new HashMap<>();
        data.put(CONSENT, "Displayed consent activity");
        FirebaseWrapper.push(LOGS, data);
    }

    public static void dailySurveyActivity() {
        Map<String, Object> data = new HashMap<>();
        data.put(DAILYSURVEY, "Displayed dailysurvey activity");
        FirebaseWrapper.push(LOGS, data);
    }

}
