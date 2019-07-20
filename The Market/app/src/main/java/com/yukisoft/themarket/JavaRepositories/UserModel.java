package com.yukisoft.themarket.JavaRepositories;

public class UserModel {
    private String id;
    private String studentNumber;
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String whatsappNum;
    private int rating;
    private int timesRated;
    private boolean registered;

    /**
     *
     * GETTER METHODS
     *
     */
    public String getId() {
        return id;
    }
    public String getStudentNumber() { return studentNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getPhoneNum() { return phoneNum; }
    public String getWhatsappNum() { return whatsappNum; }
    public int getRating() { return rating; }
    public int getTimesRated() { return timesRated; }
    public boolean isRegistered() {
        return registered;
    }

    /**
     *
     * SETTER METHODS
     *
     */
    public void setId(String id) { this.id = id; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
    public void setWhatsappNum(String whatsappNum) { this.whatsappNum = whatsappNum; }
    public void setRating(int rating) { this.rating = rating; }
    public void setTimesRated(int timesRated) { this.timesRated = timesRated; }
    public void setRegistered(boolean registered) { this.registered = registered; }

    /**
     * EMPTY CONSTRUCTOR
     */
    public UserModel() {
    }

    /**
     * MAIN CONSTRUCTOR
     * @param id
     * @param email
     * @param studentNumber
     * @param name
     * @param rating
     * @param timesRated
     * @param registered
     */
    public UserModel(String id, String email, String studentNumber, String name, int rating, int timesRated, boolean registered) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.studentNumber = studentNumber;
        this.rating = rating;
        this.timesRated = timesRated;
        this.registered = registered;
    }
}
