package edu.udg.exit.heartrate.MiBand.Utils;

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
            this.set(data[offset] + baseYear, data[offset+1], data[offset+2], data[offset+3], data[offset+4], data[offset+5]);
        }
    }

    @Override
    public String toString() {
        return "" + get(Calendar.DAY_OF_MONTH) + "/" + (get(Calendar.MONTH)+1) + "/" + get(Calendar.YEAR) +
                " " + get(Calendar.HOUR_OF_DAY) + ":" + get(Calendar.MINUTE) + ":" + get(Calendar.SECOND);
    }

}
