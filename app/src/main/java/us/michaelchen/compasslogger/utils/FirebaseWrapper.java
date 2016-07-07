package us.michaelchen.compasslogger.utils;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Map;


public class FirebaseWrapper {
    private static final String USER_DATA_KEY = "userData";

    private static DatabaseReference deviceDb = null;
    private static StorageReference deviceStore = null;

    /**
     * Initialize the Firebase connection
     */
    public static void init() {
        if(!isInit()) {
            String deviceId = PreferencesWrapper.getDeviceID();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
            DatabaseReference dbRef = db.getReference();
            deviceDb = dbRef.child(USER_DATA_KEY).child(deviceId);

            FirebaseStorage store = FirebaseStorage.getInstance();
            StorageReference storeRef = store.getReference();
            deviceStore = storeRef.child(deviceId);
        }
    }

    /**
     *
     * @return True if the Firebase connection is initialized
     */
    public static boolean isInit() {
        return deviceDb != null && deviceStore != null;
    }

    /**
     * Push the data to the Firebase backend.
     * @param key Identifying key
     * @param data Label-value mapping of data to be submitted
     */
    public static void push(String key, Map<String, Object> data) {
        if(isInit()) {
            // Pushes to the Firebase with a hierarchy as follows:
            /*
            *   UUID
            *       Sensor
            *           TimeStamp
            *               DataEntry.
            * */
            String timeStamp = DataTimeFormat.current();
            deviceDb.child(key).child(timeStamp).setValue(data);
        }
    }

    /**
     * Upload the file to the Firebase Storage backend
     * @param file Reference to the file to upload
     */
    public static void upload(File file) {
        if(isInit()) {
            Uri uri = Uri.fromFile(file);
            StorageReference fileStore = deviceStore.child(uri.getLastPathSegment());
            fileStore.putFile(uri);
        }
    }
}
