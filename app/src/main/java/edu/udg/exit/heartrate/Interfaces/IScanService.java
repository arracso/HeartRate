package edu.udg.exit.heartrate.Interfaces;

/**
 * Interface of scan service.
 */
public interface IScanService {

    ///////////////
    // Constants //
    ///////////////

    int SCAN_PERIOD = 5000; // delayMillis

    /////////////
    // Methods //
    /////////////

    /**
     * Gets the scan view.
     * @return ScanView.
     */
    IScanView getScanView();

    /**
     * Sets the view that will be used to place the found devices.
     * @param view Scan View to be set.
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

}
