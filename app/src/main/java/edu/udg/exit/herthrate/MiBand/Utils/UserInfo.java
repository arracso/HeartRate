package edu.udg.exit.herthrate.MiBand.Utils;

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
    public UserInfo(){
        blueToothAddress = null;
        username = null;
        gender = GENDER_OTHER;
        age = 20;
        height = 175;
        weight = 70;
        type = 0;
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

    public byte[] getData(DeviceInfo deviceInfo) {
        byte[] data = new byte[20];

        int userID = Integer.parseInt(username);

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

        int aliasFrom = 9;
        if (!deviceInfo.isMili1()) {
            data[9] = (byte) (deviceInfo.feature & 255);
            data[10] = (byte) (deviceInfo.appearance & 255);
            aliasFrom = 11;
        }

        byte[] aliasBytes = alias.substring(0, Math.min(alias.length(), 19 - aliasFrom)).getBytes();
        System.arraycopy(aliasBytes, 0, sequence, aliasFrom, aliasBytes.length);

        byte[] crcSequence = Arrays.copyOf(sequence, 19);
        data[19] = (byte) ((CheckSums.getCRC8(crcSequence) ^ Integer.parseInt(this.btAddress.substring(this.btAddress.length() - 2), 16)) & 0xff);


        return data;
    }

}
