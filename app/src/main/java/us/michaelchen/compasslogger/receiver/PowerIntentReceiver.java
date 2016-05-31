package us.michaelchen.compasslogger.receiver;

import us.michaelchen.compasslogger.datarecorder.PowerRecordingService;

/**
 * Created by ioreyes on 5/27/16.
 */
public class PowerIntentReceiver extends AbstractIntentReceiver {
    @Override
    protected Class getTargetService() {
        return PowerRecordingService.class;
    }
}
