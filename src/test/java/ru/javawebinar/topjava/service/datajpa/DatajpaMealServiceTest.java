package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(Profiles.DATAJPA)
public class DatajpaMealServiceTest extends MealServiceTest {

    @Test
    public void getMealWithUser() throws Exception {
        Meal meal = service.getMealWithUser(ADMIN_MEAL_ID + 1, ADMIN_ID);
        Meal mealToCompare = ADMIN_MEAL2;
        mealToCompare.setUser(ADMIN);
        MEAL_WITH_USER_MATCHER.assertMatch(meal, mealToCompare);
    }

    @Test
    public void getUserWithMealsNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(MealTestData.NOT_FOUND, USER_ID));
    }

    @Test
    public void getUserWithMealsNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(MEAL1_ID, ADMIN_ID));
    }

}
