package edu.udg.exit.heartrate.Interfaces;

/**
 * Interface of device view.
 */
public interface IDeviceView {

    /**
     * Sets the value of the heart rate on the device view.
     * @param heartRate - Heart rate value
     */
    void setHeartRateMeasure(int heartRate);

    /**
     * Sets the value of the device battery on the device view.
     * @param battery - DEvice battery percent value
     */
    void setBatteryLevel(int battery);

}
