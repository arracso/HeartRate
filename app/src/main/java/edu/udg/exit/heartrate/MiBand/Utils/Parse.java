package edu.udg.exit.heartrate.MiBand.Utils;

public class Parse {

    /**
     * Get an int from raw data bytes.
     * @param data - Raw data containing the bytes
     * @return The int stored in bytes on the raw data.
     */
    public static int BytesToInt(byte[] data) {
        return BytesToInt(data,0);
    }

    /**
     * Get an int from raw data bytes from the byte "offset".
     * @param data - Raw data containing the bytes
     * @param offset - Byte position to start parsing
     * @return The int stored in bytes on the raw data.
     */
    public static int BytesToInt(byte[] data, int offset) {
        return BytesToInt(data,offset,(data.length - offset));
    }

    /**
     * Get an int from raw data bytes from the byte "offset" and with "length" bytes.
     * @param data - Raw data containing the bytes
     * @param offset - Byte position to start parsing
     * @param length - Number of bytes to be parsed
     * @return The int stored in bytes on the raw data.
     */
    public static int BytesToInt(byte[] data, int offset, int length) {
        int id = 0;
        for(int i=0; i<length; i++){
            id = id + ((data[i + offset] & 0x0ff) << (i * 8));
        }
        return id;
    }

}
