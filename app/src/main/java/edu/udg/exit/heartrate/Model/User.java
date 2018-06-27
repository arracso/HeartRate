package edu.udg.exit.heartrate.Model;

import java.util.Date;

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

    /**
     * Constructor by copy.
     * @param user - User to be copied
     */
    public User(User user) {
        this.id = user.id;
        this.sex = user.sex;
        this.birthYear = user.birthYear;
        this.weight = user.weight;
        this.height = user.height;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSex() {
        return sex;
    }
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getBirthYear() {
        return birthYear;
    }
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }

}
