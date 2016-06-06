package us.michaelchen.compasslogger.datarecorder;

import android.app.IntentService;
import android.content.Intent;

import com.firebase.client.Firebase;

import java.util.Map;

import us.michaelchen.compasslogger.utils.FirebaseWrapper;

/**
 * Created by ioreyes on 5/24/16.
 */
public abstract class AbstractRecordingService extends IntentService {
    private static Firebase deviceDb = null;

    protected String tag = null;

    protected AbstractRecordingService(String subclassName) {
        super(subclassName);
        tag = subclassName;
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        // Get the Firebase handle if it hasn't been locally initialized yet
        if(deviceDb == null && FirebaseWrapper.isInit()) {
            deviceDb = FirebaseWrapper.getDb();
        }

        Map<String, Object> data = readData(intent);
        if(data != null) {
            FirebaseWrapper.push(broadcastKey(), data);
        }
    }

    /**
     * Defines the unique identifying broadcast key for a given data soure
     * @return The unique identifying key
     */
    protected abstract String broadcastKey();

    /**
     * Reads the system data requested by the intent
     * @param intent
     * @return A map of labeled readouts from the data source
     */
    protected abstract Map<String, Object> readData(Intent intent);
}
