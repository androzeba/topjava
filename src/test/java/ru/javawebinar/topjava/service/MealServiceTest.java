package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
        "classpath:spring/spring-app-jdbc.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Autowired
    private MealRepository repository;

    @Test
    public void get() throws Exception {
        Meal meal = service.get(MEAL_100010.getId(), ADMIN_ID);
        assertMatch(meal, MEAL_100010);
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotValidUser() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_100004.getId(), ADMIN_ID));
    }

    @Test
    public void delete() throws Exception {
        service.delete(MEAL_100002.getId(), USER_ID);
        assertNull(repository.get(MEAL_100002.getId(), USER_ID));
    }

    @Test
    public void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteNotValidUser() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_100003.getId(), ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() throws Exception {
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2020, 1, 30), LocalDate.of(2020, 1, 30), USER_ID);
        assertMatch(all, MEAL_100004, MEAL_100003, MEAL_100002);
    }

    @Test
    public void getAll() throws Exception {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, MEAL_100008, MEAL_100007, MEAL_100006, MEAL_100005, MEAL_100004, MEAL_100003, MEAL_100002);
    }

    @Test
    public void getAllNotValidUser() throws Exception {
        List<Meal> all = service.getAll(WRONG_USER_ID);
        assertTrue(all.isEmpty());
    }

    @Test
    public void update() throws Exception {
        Meal updated = getUpdated();
        service.update(updated, ADMIN_ID);
        assertMatch(service.get(MEAL_100009.getId(), ADMIN_ID), updated);
    }

    @Test
    public void updateNotValidUser() throws Exception {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, WRONG_USER_ID));
    }

    @Test
    public void create() throws Exception {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, ADMIN_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, ADMIN_ID), newMeal);
    }
}