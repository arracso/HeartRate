package edu.udg.exit.herthrate.MiBand.Utils;

public class DeviceInfo {

    ////////////////
    // Attributes //
    ////////////////

    private String id;
    private Integer profileVersion;

    // Hardaware and firmware
    private Integer firmwareVersion;
    private Integer hardwareVersion;
    private Integer feature;
    private Integer appearance;

    // Hearth rate monitoring
    private Integer firmwareVersion2;
    private boolean test1AHRMode;

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Constructor
     * @param data
     */
    public DeviceInfo(byte[] data) {
        boolean checked = isChecksumCorrect(data);

        if((data.length == 16 || data.length == 20) && checked){
            id = String.format("%02X:%02X:%02X:%02X-%02X%02X%02X%02X", data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
            profileVersion = getInt(data,8);
            firmwareVersion = getInt(data, 12);
            hardwareVersion = data[6] & 255;
            appearance = data[5] & 255;
            feature = data[4] & 255;
        }else{
            id = null;
            profileVersion = null;
            firmwareVersion = null;
            hardwareVersion = null;
            feature = null;
            appearance = null;
        }

        if(data.length == 20 && checked){
            int s = 0;
            for (int i = 0; i < 4; ++i) {
                s |= (data[16 + i] & 255) << i * 8;
            }
            firmwareVersion2 = s;
        }else{
            firmwareVersion2 = null;
        }
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Checks if the information is correct.
     * @param data
     * @return
     */
    private boolean isChecksumCorrect(byte[] data) {
        class CheckSum {
            public int getCRC8(byte[] data){
                int len = data.length;
                int i = 0;
                byte crc = 0x00;

                while (len-- > 0) {
                    byte extract = data[i++];
                    for (byte tempI = 8; tempI != 0; tempI--) {
                        byte sum = (byte) ((crc & 0xff) ^ (extract & 0xff));
                        sum = (byte) ((sum & 0xff) & 0x01);
                        crc = (byte) ((crc & 0xff) >>> 1);
                        if (sum != 0) crc = (byte) ((crc & 0xff) ^ 0x8c);
                        extract = (byte) ((extract & 0xff) >>> 1);
                    }
                }
                return (crc & 0xff);
            }
        }
        CheckSum checkSum = new CheckSum();

        int crc8 = checkSum.getCRC8(new byte[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6]});
        return (data[7] & 255) == (crc8 ^ data[3] & 255);
    }

    /**
     * Gets an int from "data" bytes starting at the position "from" and using 4 bytes.
     * @param data - Bytes containing the int and some other information
     * @param from
     * @return
     */
    private int getInt(byte[] data, int from) {
        return getInt(data, from, 4);
    }

    /**
     * Gets an int from "data" bytes starting at the position "from" and using "len" bytes.
     * @param data - Bytes containing the int and some other information
     * @param from
     * @param len - Number of bytes used to made the int
     * @return
     */
    private int getInt(byte[] data, int from, int len) {
        int ret = 0;
        for (int i = 0; i < len; ++i) {
            ret |= (data[from + i] & 255) << i * 8;
        }
        return ret;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    @Override
    public String toString() {
        return "\n\tID: " + id + " | Version: " + profileVersion + " / " + firmwareVersion + " / " + hardwareVersion +
                "\n\tFeature: " + feature + " | Appearance: " + appearance;
    }
}
