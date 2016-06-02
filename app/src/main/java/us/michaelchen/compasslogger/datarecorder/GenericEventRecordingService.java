package us.michaelchen.compasslogger.datarecorder;

import android.content.Intent;

import java.util.LinkedHashMap;
import java.util.Map;

import us.michaelchen.compasslogger.utils.DataTimeFormat;

/**
 * Created by ioreyes on 5/24/16.
 */
public class GenericEventRecordingService extends AbstractRecordingService {
    private static final Map<String, Object> EVENT_BUFFER = new LinkedHashMap<>();

    public static final String BUFFER_EXTRA = "GenericEventRecordingService.buffer_extra";

    public GenericEventRecordingService() {
        super("GenericEventRecordingService");
    }

    @Override
    protected String broadcastKey() {
        return "generic";
    }

    @Override
    protected Map<String, Object> readData(Intent intent) {
        if(intent.hasExtra(BUFFER_EXTRA)) {
            EVENT_BUFFER.put(DataTimeFormat.current(), intent.getAction());

            return null;
        } else {
            // Only transmit non-empty activity buffers
            if(!EVENT_BUFFER.isEmpty()) {
                Map<String, Object> copy = new LinkedHashMap<>(EVENT_BUFFER);
                EVENT_BUFFER.clear();

                return copy;
            } else {

                return null;
            }
        }
    }
}
