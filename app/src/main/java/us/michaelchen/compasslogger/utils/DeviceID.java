package us.michaelchen.compasslogger.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Created by ioreyes on 5/23/16.
 */
public class DeviceID {
    private static final String TAG = "DeviceID";

    private static final String UUID_FILE_NAME = "UUID.txt";
    private static String id = null;

    /**
     *
     * @param c Android context
     * @return A random UUID. If this is the first time this installation has been run, then generate
     * a new UUID and save it for future use.
     */
    public static String get(Context c) {
        // Check if there's already an ID
        if(id == null) {
            try {
                // Try to read a saved one from disk
                id = readID(c);
            } catch(IOException e) {
                // If unavailable, generate a new one and save it to disk
                id = generateID();
                writeID(c, id);
            }
        }

        return id;
    }

    /**
     *
     * @param c Android context
     * @return A UUID generated from hashes of the device, Android, and SIM IDs. This always produces
     * the same UUID for a given device, which would allow that device to be re-identified in the future.
     */
    public static String getLegacy(Context c) {
        final TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(c.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    /**
     *
     * @return A randomly generated UUID
     */
    private static String generateID() {
        UUID random = UUID.randomUUID();
        return random.toString();
    }

    /**
     *
     * @param c Android context
     * @return A previously generated UUID retrieved from persistent storage
     * @throws IOException If a previously generate UUID is unavailable or unreadable
     */
    private static String readID(Context c) throws IOException{
        FileInputStream inputStream = c.openFileInput(UUID_FILE_NAME);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputReader);

        // Read just the top line
        String id = reader.readLine();

        reader.close();
        inputReader.close();
        inputStream.close();

        // Simple validity check: the ID is not empty
        if(id != null && !id.isEmpty()) {
            return id;
        } else {
            throw new IOException("Invalid saved UUID");
        }
    }

    /**
     *
     * @param c Android context
     * @param id Randomly generated device UUID
     */
    private static void writeID(Context c, String id){
        try {
            FileOutputStream outputStream = c.openFileOutput(UUID_FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            BufferedWriter writer = new BufferedWriter(outputWriter);

            // Write just one line
            writer.write(id);

            writer.close();
            outputWriter.close();
            outputStream.close();
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
        }
    }
}
