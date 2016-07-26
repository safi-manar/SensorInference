package edu.berkeley.icsi.sensormonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.berkeley.icsi.sensormonitor.utils.PreferencesWrapper;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        programButtons(this);
        updateInfo();
    }

    private void programButtons(final Context context) {

        final Button doneButton = (Button) findViewById(R.id.info_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Make the Done button button take the user to the home screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
    }


    /**
     * Updates the textView objects (Verification Code, ID, and Build)
     * on the InfoActivity layout to display the proper information.
     */
    private void updateInfo() {
        updateVerification();
        updateUUID();
        updateBuild();
    }


    private void updateVerification() {
        TextView verifView= (TextView) findViewById(R.id.info_verif_code);

        String vCode = PreferencesWrapper.getVerifCode();

        String text = getString(R.string.info_verif_code);
        // Concat the verification code
        text = text + " " + vCode;
        verifView.setText(text);
    }

    private void updateUUID() {
        TextView uuidView= (TextView) findViewById(R.id.info_id);

        String uuid = PreferencesWrapper.getDeviceID();

        String text = getString(R.string.info_id);
        // Concat the uuid
        text = text + " " + uuid;
        uuidView.setText(text);
    }


    private void updateBuild() {
        TextView buildView= (TextView) findViewById(R.id.info_build);

        String build = getString(R.string.app_version);

        String text = getString(R.string.info_build);
        // Concat the build
        text = text + " " + build;
        buildView.setText(text);
    }

}
