package us.michaelchen.compasslogger.deadline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import us.michaelchen.compasslogger.R;

/**
 * Created by Manar on 6/3/2016.
 */


/* DeadlineActivity is called when the timer has reached the deadline. It prompts the
* user with a dialog that takes the user to uninstall the app.*/
public class DeadlineActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DeadlineActivity", "DeadlineActivity successfully started.");
        promptUninstallDialog();
    }


    private void promptUninstallDialog() {
        AlertDialog uninstallDialog = createUninstallDialog();
        uninstallDialog.show();

        Log.d("DeadlineActivity", "Passed dialog.show()");
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

        /*To avoid a bug where the back button on the dialog takes the user to the
        * transparent activity screen where interaction does nothing (and gives the
        * impression that the phone is locked up / crashing), override the back button
        * to close the activity. */
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("DeadlineActivity", "Back button pressed inside of dialog.");
                moveTaskToBack(true);
                finish();
            }
        });

        return builder.create();
    }



}

