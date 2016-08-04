package edu.berkeley.icsi.sensormonitor.periodicservices.dailysurveys;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.berkeley.icsi.sensormonitor.InfoActivity;
import edu.berkeley.icsi.sensormonitor.R;

public class DailyPostPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_post_page);
        programButtons(this);
    }

    private void programButtons(final Context context) {

        final Button doneButton = (Button) findViewById(R.id.dailypost_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Make the Done button button take the user to the home screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });

        final Button infoButton = (Button) findViewById(R.id.dailypost_info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Make the info button button take the user to the Info screen
                Intent intent = new Intent(context, InfoActivity.class);
                startActivity(intent);
            }
        });
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
