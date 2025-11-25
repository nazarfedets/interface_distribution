package org.example.inferface_distribution;

public class UserSession {

    private static String currentUserLogin;
    private static String currentUserPib;

    public static void setCurrentUser(String login, String pib) {
        currentUserLogin = login;
        currentUserPib = pib;
    }

    public static String getCurrentUserLogin() {
        return currentUserLogin;
    }

    public static String getCurrentUserPib() {
        return currentUserPib;
    }

    public static void clear() {
        currentUserLogin = null;
        currentUserPib = null;
    }
}
