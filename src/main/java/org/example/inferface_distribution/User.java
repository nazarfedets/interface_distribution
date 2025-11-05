package org.example.inferface_distribution;


public class User {
    private String login;
    private String password;
    private String pib;
    private String phone;
    private String course;
    private String groupName;
    private String role;

    public User(String login, String password, String pib, String phone, String course, String groupName, String role) {
        this.login = login;
        this.password = password;
        this.pib = pib;
        this.phone = phone;
        this.course = course;
        this.groupName = groupName;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getPib() {
        return pib;
    }

    public String getPhone() {
        return phone;
    }

    public String getCourse() {
        return course;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getRole() {
        return role;
    }
}