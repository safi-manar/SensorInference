package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 5/25/16.
 */
public class StepSensorRecordingService extends AbstractSensorRecordingService {
    public StepSensorRecordingService() {
        super("StepSensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_STEP_COUNTER;
    }

    @Override
    protected String broadcastKey() {
        return "steps";
    }
}
