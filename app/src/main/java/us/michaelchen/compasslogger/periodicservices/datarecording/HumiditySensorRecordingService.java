package us.michaelchen.compasslogger.periodicservices.datarecording;

import android.hardware.Sensor;

/**
 * Created by ioreyes on 6/30/16.
 */
public class HumiditySensorRecordingService extends AbstractSensorRecordingService {
    public HumiditySensorRecordingService() {
        super("HumiditySensorRecordingService");
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_RELATIVE_HUMIDITY;
    }

    @Override
    protected String broadcastKey() {
        return "humidity";
    }
}
