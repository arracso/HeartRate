package edu.udg.exit.heartrate.Devices.MiBand.Utils;

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
     * Constructor by params.
     * @param year of the date
     * @param month of the year
     * @param day of the month
     */
    public MiDate(int year, int month, int day) {
        super(year,month,day);
    }

    /**
     * Constructor using raw byte data.
     * @param data - date stored as bytes (only 2 digit year)
     */
    public MiDate(byte[] data) {
        this(data,0);
    }

    /**
     * Constructor using raw byte data and an offset.
     * @param data - date stored as bytes (only 2 digit year)
     * @param offset - starting index of the data bytes array
     */
    public MiDate(byte[] data, int offset) {
        this(data,offset,2000);
    }

    /**
     * Constructor using raw byte data, an offset and a base year.
     * @param data - date stored as bytes (only 2 digit year)
     * @param offset - starting index of the data bytes array
     * @param baseYear - base year for the date
     */
    public MiDate(byte[] data, int offset, int baseYear) {
        super();
        if (data.length - offset >= 6){
            this.set(data[offset] + baseYear, data[offset+1], data[offset+2], data[offset+3], data[offset+4], data[offset+5]);
        }
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Gets raw data representing a date to write into the Mi Band
     * @return Raw data bytes.
     */
    public byte[] getData() {
        return getData(2000);
    }

    /**
     * Gets raw data representing a date to write into the Mi Band
     * @param baseYear - Base Year to be subtract from the Year.
     * @return Raw data bytes.
     */
    public byte[] getData(int baseYear) {
        byte[] data = new byte[6];

        data[0] = (byte) (0xff & (get(Calendar.YEAR) - baseYear));
        data[1] = (byte) (0xff & get(Calendar.MONTH));
        data[2] = (byte) (0xff & get(Calendar.DAY_OF_MONTH));
        data[3] = (byte) (0xff & get(Calendar.HOUR_OF_DAY));
        data[4] = (byte) (0xff & get(Calendar.MINUTE));
        data[5] = (byte) (0xff & get(Calendar.SECOND));

        return data;
    }

    @Override
    public String toString() {
        return "" + get(Calendar.DAY_OF_MONTH) + "/" + (get(Calendar.MONTH)+1) + "/" + get(Calendar.YEAR) +
                " " + get(Calendar.HOUR_OF_DAY) + ":" + get(Calendar.MINUTE) + ":" + get(Calendar.SECOND);
    }

}
