package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.ArrayList;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(Profiles.DATAJPA)
public class DatajpaUserServiceTest extends UserServiceTest {

    @Test
    public void getUserWithMeals() throws Exception {
        User user = service.getUserWithMeals(USER_ID);
        USER_MATCHER.assertMatch(user, USER);
        MEAL_MATCHER.assertMatch(user.getMeals(), MEAL7, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void userWithNoMeals() throws Exception {
        User user = service.getUserWithMeals(NO_MEAL_USER_ID);
        USER_MATCHER.assertMatch(user, NO_MEAL_USER);
        MEAL_MATCHER.assertMatch(user.getMeals(), new ArrayList<>());
    }

    @Test
    public void userWithMealsNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.getUserWithMeals(MealTestData.NOT_FOUND));
    }
}
