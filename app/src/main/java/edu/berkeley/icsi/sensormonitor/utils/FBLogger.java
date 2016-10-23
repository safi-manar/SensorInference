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


    /****************************************************************************
     *                                                                          *
     * All log methods automatically log timestamp via FirebaseWrapper.push()   *
     *                                                                          *
     ****************************************************************************/

    public static void consentActivity() {
        Map<String, Object> data = new HashMap<>();
        data.put(CONSENT, "Displayed consent activity");
        FirebaseWrapper.push(LOGS, data);
    }


    /****************************************************************************
     * Logging for Daily Survey                                                 *
     ****************************************************************************/

    // Log when the daily survey activity is displayed
    public static void dailySurveyActivity() {
        Map<String, Object> data = new HashMap<>();
        data.put(DAILYSURVEY, "Displayed dailysurvey activity");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user postpones the daily survey
    public static void dailySurveyPostponed() {
        Map<String, Object> data = new HashMap<>();
        data.put(DAILYSURVEY, "Postponed daily survey");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user chooses to abstain from the survey
    public static void dailySurveyAbstained() {
        Map<String, Object> data = new HashMap<>();
        data.put(DAILYSURVEY, "Abstained daily survey");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user accepts to complete the survey (does not mean the use *has* submitted it)
    public static void dailySurveyAccepted() {
        Map<String, Object> data = new HashMap<>();
        data.put(DAILYSURVEY, "Accepted daily survey");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user submitted the daily survey
    public static void dailySurveySubmitted() {
        Map<String, Object> data = new HashMap<>();
        data.put(DAILYSURVEY, "Submitted daily survey");
        FirebaseWrapper.push(LOGS, data);
    }


    /****************************************************************************
     * Logging for Exit (Deadline) Survey                                       *
     ****************************************************************************/
    // Log when the deadline activity is displayed
    public static void deadlineActivity() {
        Map<String, Object> data = new HashMap<>();
        data.put(DEADLINESURVEY, "Displayed deadline activity");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user postpones the deadline survey
    public static void deadlineSurveyPostponed() {
        Map<String, Object> data = new HashMap<>();
        data.put(DEADLINESURVEY, "Postponed deadline survey");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user chooses to abstain from the survey and proceeds to uninstall
    public static void deadlineSurveyAbstained() {
        Map<String, Object> data = new HashMap<>();
        data.put(DEADLINESURVEY, "Abstained deadline survey");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user accepts to complete the survey (does not mean the use *has* submitted it)
    public static void deadlineSurveyAccepted() {
        Map<String, Object> data = new HashMap<>();
        data.put(DEADLINESURVEY, "Accepted deadline survey");
        FirebaseWrapper.push(LOGS, data);
    }

    // Log when the user submitted the deadline survey
    public static void deadlineSurveySubmitted() {
        Map<String, Object> data = new HashMap<>();
        data.put(DEADLINESURVEY, "Submitted deadline survey");
        FirebaseWrapper.push(LOGS, data);
    }


}
