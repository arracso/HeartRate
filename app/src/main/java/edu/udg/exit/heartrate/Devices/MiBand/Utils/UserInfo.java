package edu.udg.exit.heartrate.Devices.MiBand.Utils;

import android.util.Log;

import java.util.Arrays;

/**
 * Class to handle user information from mi band.
 */
public class UserInfo {

    ///////////////
    // Constants //
    ///////////////

    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_OTHER = 2;

    ////////////////
    // Attributes //
    ////////////////

    private String blueToothAddress; // address of the mi band

    private String username;
    private Integer userID;
    private Integer gender;
    private Integer age;
    private Integer height;
    private Integer weight;

    private Integer type; // type of the device ??

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Default Constructor.
     */
    public UserInfo() {
        blueToothAddress = null;
        username = null;
        userID = null;
        gender = GENDER_OTHER;
        age = 20;
        height = 175;
        weight = 70;
        type = 0;
    }

    /**
     * Constructor by data.
     * @param data - user information
     */
    public UserInfo(byte[] data) {
        userID = Parse.BytesToInt(data,0,4);
        username = null;
        gender = (int) (data[4] & 0x0ff);
        age = (int) (data[5] & 0x0ff);
        height = (int) (data[6] & 0x0ff);
        weight = (int) (data[7] & 0x0ff);
        type = (int) (data[8] & 0x0ff);
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Calculate user ID from username.
     * @return userID
     */
    private int calculateUserID() {
        try {
            return Integer.parseInt(username,8);
        } catch (NumberFormatException e) {
            Log.w("UserInfo", "Using hash code.");
            return username.hashCode();
        }
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /*---------*/
    /* Setters */
    /*---------*/

    /**
     * Sets the bluetooth address to connect to.
     * @param address of the mi band
     */
    public void setBlueToothAddress(String address) {
        this.blueToothAddress = address;
    }

    /**
     * Sets the username.
     * @param username of the user
     */
    public void setUsername(String username) {
        this.username = username;
        this.userID = calculateUserID();
    }

    /**
     * Sets the gender.
     * @param gender of the user
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * Sets the age.
     * @param age of the user
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Sets the height.
     * @param height of the user
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * Sets the weight.
     * @param weight of the user
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * Sets the type.
     * @param type of the device ???
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /*---------*/
    /* Getters */
    /*---------*/

    /**
     * Gets the data bytes for user information.
     * @param deviceInfo information of the connected device
     * @return some deviceInfo and userInfo in Bytes
     */
    public byte[] getData(DeviceInfo deviceInfo) {
        byte[] data = new byte[20];

        // Put userID
        int userID = calculateUserID();
        data[0] = (byte) userID;
        data[1] = (byte) (userID >>> 8);
        data[2] = (byte) (userID >>> 16);
        data[3] = (byte) (userID >>> 24);

        // Put personal information
        data[4] = (byte) (gender & 0xff);
        data[5] = (byte) (age & 0xff);
        data[6] = (byte) (height & 0xff);
        data[7] = (byte) (weight & 0xff);

        // Put type ??? mi band ???
        data[8] = (byte) (type & 0xff);

        int usernameFrom = 9;
        // Put feature and appearance
        if (!deviceInfo.isMili1()) {
            data[9] = (byte) (deviceInfo.getFeature() & 255);
            data[10] = (byte) (deviceInfo.getAppearance() & 255);
            usernameFrom = 11;
        }

        // Put username
        byte[] usernameBytes = username.substring(0, Math.min(username.length(), 19 - usernameFrom)).getBytes();
        System.arraycopy(usernameBytes, 0, data, usernameFrom, usernameBytes.length);

        // Put crc8
        byte[] crc = Arrays.copyOf(data, 19);
        data[19] = (byte) ((CheckSums.getCRC8(crc) ^ Integer.parseInt(this.blueToothAddress.substring(this.blueToothAddress.length() - 2), 16)) & 0xff);

        return data;
    }

    @Override
    public String toString() {
        return "UserID: " + userID + " | Gender: " + gender + "\n"
                + "Age: " + age + " | Height: " + height + " | Weight: " + weight;
    }

}
