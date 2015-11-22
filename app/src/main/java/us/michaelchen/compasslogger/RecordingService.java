package us.michaelchen.compasslogger;

import android.app.IntentService;
import android.content.Context;

import com.firebase.client.Firebase;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public abstract class RecordingService extends IntentService {

    protected Firebase firebase;
    protected String deviceId;
    protected Context context;

    public static final String TIME = "time";
    public static final String USER_DATA_KEY = "userData";

    private static final String FIREBASE_URL = "https://luminous-torch-7892.firebaseio.com/";

    public RecordingService() {
        super("RecordingService");
    }

    protected void initContext(Context context) {
        this.context = context;
        deviceId = getDeviceId();
        Firebase.setAndroidContext(context);
        firebase = new Firebase(FIREBASE_URL);
    }

    public String getDeviceId() {
        return MainActivity.deviceId;
    }

}
