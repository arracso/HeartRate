package edu.udg.exit.heartrate.MiBand.Utils;

import java.util.Arrays;

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
        gender = GENDER_OTHER;
        age = 20;
        height = 175;
        weight = 70;
        type = 0;
    }

    /**
     * Constructor.
     */
    public UserInfo(byte[] data) {
        username = getUsername(data);
        gender = (int) (data[4] & 0xff);
        age = (int) (data[5] & 0xff);
        height = (int) (data[6] & 0xff);
        weight = (int) (data[7] & 0xff);
        type = (int) (data[8] & 0xff);
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Calculate the user id from username.
     * @return userID
     */
    private int getUserID() {
        try {
            return Integer.parseInt(username);
        } catch (NumberFormatException e) {
            return username.hashCode();
        }
    }

    /**
     * Gets the username from raw data.
     * @param data
     * @return
     * TODO - Correct
     */
    private String getUsername(byte[] data) {
        int id = ((int) data[3] << 24);
        if(id < 0){
            id = id - (int) data[0];
            id = id - ((int) data[1] << 8);
            id = id - ((int) data[2] << 16);
        }else{
            id = id + (int) data[0];
            id = id + ((int) data[1] << 8);
            id = id + ((int) data[2] << 16);
        }

        return Integer.toHexString(id);
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

        int userID = getUserID();

        // Put userID
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
        return "Username: " + username + " | Gender: " + gender + "\n"
                + "Age: " + age + " | Height: " + height + " | Weight: " + weight;
    }

}
