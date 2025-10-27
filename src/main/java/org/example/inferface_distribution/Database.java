package org.example.inferface_distribution;

import java.util.ArrayList;
import java.util.List;

public class Database {
    public static List<User> users = new ArrayList<>();

    static {
        users.add(new User("ivan", "123", "Іваненко Іван", "0991112222", "ЦЗ-11"));
        users.add(new User("oleg", "123", "Петренко Олег", "0993334444", "ЦЗ-12"));
        users.add(new User("victoria", "123", "Коваленко Вікторія", "0995556666", "ЦЗ-11"));
        users.add(new User("admin", "admin123", "Адміністратор", "0000000000", "ALL"));
    }

    public static User getUserByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst().orElse(null);
    }
}
