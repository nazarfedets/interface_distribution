package org.example.inferface_distribution;

public class Duty {
    private int id;
    private String userLogin;
    private int year;
    private int month;
    private int day;
    private String place;

    public Duty(int id, String userLogin, int year, int month, int day, String place) {
        this.id = id;
        this.userLogin = userLogin;
        this.year = year;
        this.month = month;
        this.day = day;
        this.place = place;
    }

    public int getId() { return id; }
    public String getUserLogin() { return userLogin; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public String getPlace() { return place; }

    public void setPlace(String place) { this.place = place; }

}
