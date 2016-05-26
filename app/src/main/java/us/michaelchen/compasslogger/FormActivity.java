package us.michaelchen.compasslogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class FormActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Intent i = getIntent();

        String deviceId = i.getStringExtra(MainActivity.INTENT_DEVICE_ID);
        String formURL = getResources().getString(R.string.gdocs_url);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(formURL + deviceId);
    }

}
