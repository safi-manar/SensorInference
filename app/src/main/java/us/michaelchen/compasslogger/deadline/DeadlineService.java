package us.michaelchen.compasslogger.deadline;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Manar on 6/3/2016.
 */
public class DeadlineService extends IntentService {

    public DeadlineService() {
        super("DeadlineService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("DeadlineService", "The deadline service has successfully started.");
    }

}

/*        Intent deadlineDialog = new Intent(this, DeadlineActivity.class);
        deadlineDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(deadlineDialog);
        Log.d("DeadlineService", "Just started the DeadlineActivity");*/
