package edu.berkeley.icsi.sensormonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SurveyRedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_redirect);

        programButtons(this);
    }

    private void programButtons(final Context context) {

        final Button agreeButton = (Button) findViewById(R.id.survey_continue);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Show the Survey Redirect Activity
                Intent intent = new Intent(context, EntrySurveyFormActivity.class);
                startActivity(intent);
            }
        });

    }
}
