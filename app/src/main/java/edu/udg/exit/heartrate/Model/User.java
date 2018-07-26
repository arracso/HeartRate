package edu.udg.exit.heartrate.Model;

/**
 * User Model Object to retrieve response from rest requests.
 */
public class User {

    ////////////////
    // Attributes //
    ////////////////

    private Integer id;
    private Integer sex; // (0|1|2) => (other|male|female)
    private Integer birthYear;
    private Integer weight;
    private Integer height;

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    /**
     * Constructor by default.
     */
    public User() {
        this.id = null;
        this.sex = null;
        this.birthYear = null;
        this.weight = null;
        this.height = null;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    public Integer getId() {
        return id;
    }

    public Integer getSex() {
        return sex;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

}
