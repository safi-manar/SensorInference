package edu.berkeley.icsi.sensormonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.berkeley.icsi.sensormonitor.utils.MasterSwitch;
import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

public class EntrySurveyFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_survey_form);

        setWebView(this);
    }

    /*Sets up the WebView in the layout to show the online survey page. */
    private void setWebView(Context context) {
        String deviceId = PreferencesWrapper.getDeviceID();
        String formURL = getResources().getString(R.string.entry_survey_url);

        // Register the survey-complete receiver.
        registerSurveyReceiver();

        WebViewClient webViewClient = getWebviewClient(context);
        WebView webView = (WebView) findViewById(R.id.survey_form_view);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(formURL + deviceId);
    }


    /**
     * Custom web client to let us override behaviour when the Google form has been submitted
     */
    private WebViewClient getWebviewClient(final Context context) {
        return new WebViewClient() {

            /**
             * Broadcast a survey-complete intent once the form has been submitted, to be
             * received by the surveyReceiver BroadCastReceiver in MainActivity.
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains(getString(R.string.survey_completion_keyword))) {
                    Intent intent = new Intent("survey-complete");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }


        };
    }

    /**
     * Register to receive survey completion intent.
     * We are registering an observer (surveyReceiver) to receive Intents
     * with actions named "survey-complete".
     */
    private void registerSurveyReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(surveyReceiver,
                new IntentFilter("survey-complete"));
    }

    /**
    * The handler for received Intents. This will be called whenever an Intent
    * with an action named "survey-complete" is broadcasted.
    * */
    private BroadcastReceiver surveyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Now that the survey has been completed, tag the user as having completed it.
            PreferencesWrapper.setSurveyCompleted();

            // Sensor data collection has begun. Now begin a one-week countdown.
            PreferencesWrapper.setUninstallDeadline();
            // Also, set today's daily survey deadline
            PreferencesWrapper.setInitialDailyDeadline();
            //Now that the user has completed the form, sensor data collection can begin.
            MasterSwitch.on(context);

            // Now, show the user the Verification Activity.
            Intent verificationIntent = new Intent(context, VerificationActivity.class);
            startActivity(verificationIntent);
        }
    };

}
