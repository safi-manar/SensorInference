package us.michaelchen.compasslogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class FormActivity extends AppCompatActivity {

    public final String FORM_BASE_URL = "https://docs.google.com/forms/d/1l7AnMbpM-NVXBObjUg4lrcSIt1VDXsfpXBcbMRKWRSI/viewform?entry.1894950560=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Intent i = getIntent();
        String deviceId = i.getStringExtra(MainActivity.INTENT_DEVICE_ID);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(FORM_BASE_URL + deviceId);
    }

}
