package edu.berkeley.icsi.sensormonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.prefs.Preferences;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        programButtons(this);
        updateVerification(PreferencesWrapper.getVerifCode());
    }

    private void programButtons(final Context context) {

        final Button agreeButton = (Button) findViewById(R.id.verif_continue);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Show the info activity screen
                Intent intent = new Intent(context, InfoActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Updates the Verification code textView object to display vCode
     */
    private void updateVerification(String vCode) {
        TextView verifView= (TextView) findViewById(R.id.verif_code);
        verifView.setText(vCode);

    }

    /**
     * Override the back button so that the user cannot go back to the survey.
     */
    @Override
    public void onBackPressed()
    {
        // Do nothing
    }
}
