package us.michaelchen.compasslogger.periodicservices.datadestination;

import android.content.Context;

import java.util.Map;

/**
 * Created by ioreyes on 6/23/16.
 */
public class DiskDestination extends AbstractDataDestination {
    private static Context context = null;

    /**
     *
     * @param context Calling Android context, necessary to access disk resources
     */
    public DiskDestination(Context context) {
        this.context = context;
    }

    @Override
    public void submit(String label, Map<String, Object> data) {

    }
}
