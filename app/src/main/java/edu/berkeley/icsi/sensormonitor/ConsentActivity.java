package edu.berkeley.icsi.sensormonitor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.berkeley.icsi.sensormonitor.utils.FBLogger;

public class ConsentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        // Make links clickable
        TextView consent = (TextView) findViewById(R.id.consent_agreement_message);
        consent.setMovementMethod(LinkMovementMethod.getInstance());

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        programButtons(this);

        // Internal logging
        FBLogger.consentActivity();

    }

    private void programButtons(final Context context) {

        final Button agreeButton = (Button) findViewById(R.id.consent_agree_button);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isPermissionGranted()) {
                    // Skip to the SurveyRedirect Activity
                    Intent intent = new Intent(context, SurveyRedirectActivity.class);
                    startActivity(intent);
                } else {
                    // Show the Permission Activity
                    Intent intent = new Intent(context, PermissionActivity.class);
                    startActivity(intent);
                }
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.consent_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConsentActivity.this.finish();
                System.exit(0);
                finish();
            }
        });
    }


    /**
     *
     * @return true if and only if full permissions have already been granted.
     */
    private boolean isPermissionGranted() {
        for (int i = 0; i < PermissionActivity.PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(this, PermissionActivity.PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


}
