package edu.berkeley.icsi.sensormonitor.periodicservices.dailysurveys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.berkeley.icsi.sensormonitor.R;
import edu.berkeley.icsi.sensormonitor.periodicservices.deadline.UninstallPromptActivity;
import edu.berkeley.icsi.sensormonitor.utils.MasterSwitch;
import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

public class DailySurveyFormActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_survey_form);
        Intent i = getIntent();

        setWebView(this);
    }


    /*Sets up the WebView in the layout to show the online survey page. */
    private void setWebView(Context context) {
        String deviceId = PreferencesWrapper.getDeviceID();
        String formURL = getResources().getString(R.string.daily_survey_url);

        // Register the daily-survey-complete receiver.
        registerSurveyReceiver();

        WebViewClient webViewClient = getWebviewClient(context);
        WebView webView = (WebView) findViewById(R.id.daily_survey_view);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(formURL + deviceId);
    }


    /**
     * Custom web client to let us override behaviour when the survey form has been submitted
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
                if (url.contains(getString(R.string.survey_form_response))) {
                    Intent intent = new Intent("daily-survey-complete");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }


        };
    }


    /**
     * Register to receive survey completion intent.
     * We are registering an observer (surveyReceiver) to receive Intents
     * with actions named "daily-survey-complete".
     */
    private void registerSurveyReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(surveyReceiver,
                new IntentFilter("daily-survey-complete"));
    }

    /**
     * The handler for received Intents. This will be called whenever the
     * "daily-survey-complete" intent is broadcasted.
     * */
    private BroadcastReceiver surveyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Now, show the user the Daily PostPage Activity.
            Intent dailyPostIntent = new Intent(context, UninstallPromptActivity.class);
            startActivity(dailyPostIntent);
        }
    };

}
