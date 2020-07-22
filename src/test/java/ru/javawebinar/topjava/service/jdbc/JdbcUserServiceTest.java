package ru.javawebinar.topjava.service.jdbc;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static ru.javawebinar.topjava.Profiles.JDBC;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(JDBC)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JdbcUserServiceTest extends AbstractUserServiceTest {

//    @Test
//    public void a1update() throws Exception {
//        User updated = getUpdated();
//        service.update(updated);
//
//        USER_MATCHER.assertMatch(service.getAll(), getUpdated(), USER);
//    }
//
//    @Test
//    public void a2getAll() throws Exception {
//        List<User> all = service.getAll();
//        USER_MATCHER.assertMatch(all, ADMIN, USER);
//    }
//
//    @Test
//    public void createdWithoutRoles() throws Exception {
//        User newUser = new User(null, "New", "new@gmail.com", "newPass", 1555, false, new Date(), new HashSet<Role>());
//        User newUserClone = new User(newUser);
//        User created = service.create(newUserClone);
//        newUser.setId(created.getId());
//        USER_MATCHER.assertMatch(service.getAll(), ADMIN, newUser, USER);
//    }
}