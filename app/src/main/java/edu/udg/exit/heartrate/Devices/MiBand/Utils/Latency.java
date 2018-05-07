package edu.udg.exit.heartrate.Devices.MiBand.Utils;

/**
 * MiBand Latency.
 */
public class Latency {

    ////////////////
    // Attributes //
    ////////////////

    private byte[] latencyBytes;

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Default Constructor.
     */
    public Latency() {
        latencyBytes = new byte[12];
    }

    /**
     * Constructor by params.
     * @param minConnectionInterval
     * @param maxConnectionInterval
     * @param latency
     * @param timeout
     * @param connectionInterval
     * @param advertisementInterval
     */
    public Latency(int minConnectionInterval, int maxConnectionInterval, int latency, int timeout, int connectionInterval, int advertisementInterval) {
        latencyBytes = new byte[12];
        latencyBytes[0] = (byte) (minConnectionInterval & 0xff);
        latencyBytes[1] = (byte) (0xff & minConnectionInterval >> 8);
        latencyBytes[2] = (byte) (maxConnectionInterval & 0xff);
        latencyBytes[3] = (byte) (0xff & maxConnectionInterval >> 8);
        latencyBytes[4] = (byte) (latency & 0xff);
        latencyBytes[5] = (byte) (0xff & latency >> 8);
        latencyBytes[6] = (byte) (timeout & 0xff);
        latencyBytes[7] = (byte) (0xff & timeout >> 8);
        latencyBytes[8] = (byte) (connectionInterval & 0xff);;
        latencyBytes[9] = (byte) (0xff & connectionInterval >> 8);
        latencyBytes[10] = (byte) (advertisementInterval & 0xff);
        latencyBytes[11] = (byte) (0xff & advertisementInterval >> 8);
    }

    /**
     * Constructor by data.
     * @param data
     */
    public Latency(byte[] data) {
        this.latencyBytes = data;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Set the bytes of the mi band latency.
     * @param minConnectionInterval
     * @param maxConnectionInterval
     * @param latency
     * @param timeout
     * @param advertisementInterval
     */
    public void setLatencyBytes(int minConnectionInterval, int maxConnectionInterval, int latency, int timeout, int advertisementInterval) {
        latencyBytes[0] = (byte) (minConnectionInterval & 0xff);
        latencyBytes[1] = (byte) (0xff & minConnectionInterval >> 8);
        latencyBytes[2] = (byte) (maxConnectionInterval & 0xff);
        latencyBytes[3] = (byte) (0xff & maxConnectionInterval >> 8);
        latencyBytes[4] = (byte) (latency & 0xff);
        latencyBytes[5] = (byte) (0xff & latency >> 8);
        latencyBytes[6] = (byte) (timeout & 0xff);
        latencyBytes[7] = (byte) (0xff & timeout >> 8);
        latencyBytes[8] = 0;
        latencyBytes[9] = 0;
        latencyBytes[10] = (byte) (advertisementInterval & 0xff);
        latencyBytes[11] = (byte) (0xff & advertisementInterval >> 8);
    }

    /**
     * Gets the bytes of the mi band latency.
     * @return
     */
    public byte[] getLatencyBytes() {
        return latencyBytes;
    }

    @Override
    public String toString() {
        return "(" + Parse.BytesToInt(latencyBytes, 0, 2) +
                ", " + Parse.BytesToInt(latencyBytes, 2, 2) +
                ", " + Parse.BytesToInt(latencyBytes, 4, 2) +
                ", " + Parse.BytesToInt(latencyBytes, 6, 2) +
                ", " + Parse.BytesToInt(latencyBytes, 8, 2) +
                ", " + Parse.BytesToInt(latencyBytes, 10, 2) +
                ")";
    }

}
