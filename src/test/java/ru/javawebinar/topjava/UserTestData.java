package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {

    public static TestMatcher<User> USER_MATCHER = TestMatcher.usingFieldsComparator("registered", "meals");

    public static final int NOT_FOUND = 10;
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;

    public static final User USER = new User(USER_ID, "User", "user@yandex.ru", "password", Role.USER);
    public static final User ADMIN = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ADMIN, Role.USER);

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", 1555, false, new Date(), Set.of(Role.USER, Role.ADMIN));
    }

    public static User getUpdated() {
        User updated = new User(ADMIN);
        updated.setName("UpdatedName");
        updated.setCaloriesPerDay(330);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        updated.setRoles(roles);
        return updated;
    }
}
