package edu.berkeley.icsi;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class SensorsTest extends ApplicationTestCase<Application> {
    private String tag = null;
    private SensorManager sensorManager = null;

    public SensorsTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        Context context = getContext();
        tag = context.getPackageName();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void testSensors() {
        assertNotNull("Logging tag is null", tag);

        for(Sensor s : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            Log.d(tag, s.getName());
        }
    }

    public void testAccelerometerPresent() {
        assertNotNull("Accelerometer not present",
                      sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    public void testBatchedAccelerometerPresent() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        assertNotNull("Accelerometer not present", accelerometer);
        assertTrue("Accelerometer batching not present",
                   accelerometer.getFifoMaxEventCount() > 0 || accelerometer.getFifoReservedEventCount() > 0);
    }

    public void testGyroscopePresent() {
        assertNotNull("Gyroscope not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    }

    public void testBatchedGyroscopePresent() {
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        assertNotNull("Gyroscope not present", gyroscope);
        assertTrue("Gyroscope batching not present",
                gyroscope.getFifoMaxEventCount() > 0 || gyroscope.getFifoReservedEventCount() > 0);
    }

    public void testHumidityPresent() {
        assertNotNull("Humidity sensor not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY));
    }

    public void testLightSensorPresent() {
        assertNotNull("Light sensor not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
    }

    public void testMagnetometerPresent() {
        assertNotNull("Magnetometer not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    public void testBarometerPresent() {
        assertNotNull("Barometer not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));
    }

    public void testProximityPresent() {
        assertNotNull("Proximity sensor not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }

    public void testRotationPresent() {
        assertNotNull("Rotation sensor not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }

    public void testStepCounterPresent() {
        assertNotNull("Step counter not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));
    }

    public void testTemperaturePresent() {
        assertNotNull("Temperature sensor not present",
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE));
    }
}