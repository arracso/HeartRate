package edu.udg.exit.herthrate.MiBand;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class to handle date conversion and reading.
 */
public class MiDate extends GregorianCalendar {

    ////////////////////////
    // Life cycle methods //
    ////////////////////////

    /**
     * Default constructor.
     */
    public MiDate() {
        super();
    }

    /**
     * Constructor using raw byte data.
     * @param data
     */
    public MiDate(byte[] data) {
        this(data,0);
    }

    /**
     * Constructor using raw byte data and offset.
     * @param data
     */
    public MiDate(byte[] data, int offset) {
        this(data,offset,2000);
    }

    /**
     * Constructor using raw byte data and offset.
     * @param data
     */
    public MiDate(byte[] data, int offset, int baseYear) {
        super();
        if (data.length - offset >= 6){
            this.set(data[0 + offset] + baseYear, data[1 + offset], data[2 + offset], data[3 + offset], data[4 + offset], data[5 + offset]);
            Log.d("Date", "Year: " + data[offset]);

            // Maybe mi band don't have the local hour
            //int offsetInHours = MiBandCoordinator.getDeviceTimeOffsetHours();
            //if(offsetInHours != 0) timestamp.add(Calendar.HOUR_OF_DAY,-offsetInHours);
        }
    }

    @Override
    public String toString() {
        return "" + get(Calendar.DAY_OF_MONTH) + "/" + (get(Calendar.MONTH)+1) + "/" + get(Calendar.YEAR) +
                " " + get(Calendar.HOUR_OF_DAY) + ":" + get(Calendar.MINUTE) + ":" + get(Calendar.SECOND);
    }

}
