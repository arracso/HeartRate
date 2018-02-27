package edu.udg.exit.heartrate.Interfaces;

public interface IScanService {

    ///////////////
    // Constants //
    ///////////////

    int SCAN_PERIOD = 3000; // delayMillis

    /////////////
    // Methods //
    /////////////

    /**
     * Sets the view that will be used to place the found devices.
     * @param view
     */
    void setScanView(IScanView view);

    /**
     * Unsets the view that was used to place the found devices.
     */
    void unSetScanView();

    /**
     * Scan Low Energy Bluetooth Devices for SCAN_PERIOD milliseconds
     * @param enable - (True -> Start | False -> Stop)
     */
    void scanLeDevice(final boolean enable);

    /**
     * Check if the service is scanning.
     * @return boolean
     */
    boolean isScanning();

    /**
     * Bind the device using its address and add to user preferences.
     * @param address - MAC address of the device.
     */
    void bindDevice(String address);


    /**
     * Unbind the binded device and remove from user preferences.
     */
    void unbindDevice();
}
