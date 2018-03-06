package edu.udg.exit.heartrate.MiBand.Utils;

/**
 * Class to obtain different check sums.
 */
public class CheckSums {

    /**
     * Get CRC8 from data
     * @param data - data to check
     * @return return the CRC8
     */
    public static int getCRC8(byte[] data){
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
