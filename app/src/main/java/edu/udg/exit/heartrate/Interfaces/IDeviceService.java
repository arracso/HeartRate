package edu.udg.exit.heartrate.Interfaces;

import java.util.Date;

/**
 * Interface of device service (bluetooth)
 */
public interface IDeviceService {

    /////////////
    // Methods //
    /////////////

    /**
     * Sets the view that will be used to show the measurement.
     * @param view - Measure view to be set.
     */
    void setDeviceView(IDeviceView view);

    /**
     * Unset device view.
     */
    void unsetDeviceView();

    /**
     * Send a command to the device to start the heart rate measurement.
     */
    void startHeartRateMeasure();

    /**
     * Send a command to the device to stop the heart rate measurement.
     */
    void stopHeartRateMeasure();

    /**
     * Sets the value of heart rate measure into device view.
     * Also save the measure to the data base.
     * @param date - Time of the measure
     * @param measure - Heart rate measure
     */
    void setHeartRateMeasure(Date date, Integer measure);

    /**
     * Sends a command to the device in order to retrieve the battery level.
     */
    void retrieveBatteryLevel();

    /**
     * Sets the value of the device battery level on the device view.
     * @param battery - Device battery level
     */
    void setBatteryLevel(Integer battery);

    /**
     * Sets the wear location of the device.
     * @param wearLocation - (0 -> left | 1 -> right | 2 -> neck)
     */
    void setWearLocation(int wearLocation);

}
