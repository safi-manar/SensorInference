package us.michaelchen.compasslogger.receiver;

import us.michaelchen.compasslogger.datarecorder.GenericEventRecordingService;

/**
 * Created by ioreyes on 5/31/16.
 */
public class GenericIntentReceiver extends AbstractIntentReceiver {
    @Override
    protected Class getTargetService() {
        return GenericEventRecordingService.class;
    }
}
