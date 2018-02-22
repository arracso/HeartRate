package edu.udg.exit.herthrate.MiBand;

import java.util.GregorianCalendar;

/**
 * Class to handle battery information reading.
 */
public class BatteryInfo {

    ///////////////
    // Constants //
    ///////////////

    private static final int UNKNOWN = -1;

    private static final int NORMAL = 0;
    private static final int LOW = 1;
    private static final int CHARGING = 2;
    private static final int CHARGING_FULL = 3;
    private static final int CHARGE_OFF = 4;

    ////////////////
    // Attributes //
    ////////////////

    private Integer percent;
    private Integer state;
    private Integer numCharges;
    private GregorianCalendar lastCharge;

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    /**
     * Constructor.
     * @param data
     */
    public BatteryInfo(byte[] data) {
        if(data.length >= 1) percent = new Integer(data[0]);
        else percent = null;

        if(data.length >= 10){
            state = new Integer(data[9]);
            numCharges = ((0xff & data[7]) | ((0xff & data[8]) << 8));
            lastCharge = new MiDate(data,1);
        }else{
            state = null;
            numCharges = null;
            lastCharge = null;
        }
    }

    ////////////////////
    // Public methods //
    ////////////////////

    /**
     * Gets the percent of battery level.
     * @return
     */
    public Integer getPercent() {
        if(percent == null) return null;
        else if(percent > 100) return 100;
        else if(percent < 0) return 0;
        else return percent;
    }

    /**
     * Gets the number of times the device has been charged.
     * @return
     */
    public Integer getNumberOfCharges() {
        return numCharges;
    }

    /**
     * Gets the state of the battery.
     * @return
     */
    public Integer getState() {
        switch (state != null ? state : UNKNOWN){
            case LOW:
                return LOW;
            case NORMAL:
                return NORMAL;
            case CHARGING:
                return CHARGING;
            case CHARGING_FULL:
                return CHARGING_FULL;
            case CHARGE_OFF:
                return CHARGE_OFF;
            case UNKNOWN:
                return UNKNOWN;
        }
        return state;
    }

    /**
     * Gets the last time the device was charged.
     * @return
     */
    public GregorianCalendar getLastCharge() {
        return lastCharge;
    }
}
