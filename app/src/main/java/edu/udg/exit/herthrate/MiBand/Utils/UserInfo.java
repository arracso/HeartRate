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

    private Integer type;

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
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the gender.
     * @param gender
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * Sets the age.
     * @param age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Sets the height.
     * @param height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * Sets the weight.
     * @param weight
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * Sets the type.
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }
}
