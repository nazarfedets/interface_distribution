package org.example.inferface_distribution;

public class User {
    private String login;
    private String password;
    private String pib;
    private String phone;
    private String group;

    public User(String login, String password, String pib, String phone, String group) {
        this.login = login;
        this.password = password;
        this.pib = pib;
        this.phone = phone;
        this.group = group;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getPib() { return pib; }
    public String getPhone() { return phone; }
    public String getGroup() { return group; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPib(String pib) {
        this.pib = pib;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}