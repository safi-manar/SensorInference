package edu.berkeley.icsi.sensormonitor.periodicservices.deadline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import edu.berkeley.icsi.sensormonitor.R;

public class UninstallPromptActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptUninstallDialog();
    }

    private void promptUninstallDialog() {
        AlertDialog uninstallDialog = createUninstallDialog();
        uninstallDialog.show();
    }

    /*Constructs the AlertDialog*/
    private AlertDialog createUninstallDialog() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNeutralButton(R.string.uninstall_proceed_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        });
        builder.setMessage(R.string.uninstall_message).setTitle(R.string.uninstall_message_title);


        // Prevent users from dismissing the dialog by tapping outside it
        //builder.setCancelable(false);

        return builder.create();
    }


    /**
     * To avoid a bug where the back button on the dialog takes the user to the
     * transparent activity screen where interaction does nothing (and gives the
     * impression that the phone is locked up / crashing), override the back button
     * to close the activity.
     */
    @Override
    public void onBackPressed()
    {
        Log.d("UninstallPromptActivity", "Back button pressed inside of dialog.");
        moveTaskToBack(true);
        finish();
    }


}
