package us.michaelchen.compasslogger.receiver;

import us.michaelchen.compasslogger.datarecorder.ScreenRecordingService;

/**
 * Created by ioreyes on 5/27/16.
 */
public class ScreenIntentReceiver extends AbstractIntentReceiver {
    @Override
    protected Class getTargetService() {
        return ScreenRecordingService.class;
    }
}
