package us.michaelchen.compasslogger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.michaelchen.compasslogger.periodicservices.datarecording.LocationRecordingService;

/**
 * Created by ioreyes on 6/21/16.
 */
public class PeriodicGroundTruthReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Starts the service to report ground truth location data
        Intent i = new Intent(context, LocationRecordingService.class);
        context.startService(i);
    }
}
