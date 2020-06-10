package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryMapMealDao implements MealDao {
    private static final Logger log = LoggerFactory.getLogger(MemoryMapMealDao.class);

    private final AtomicInteger id = new AtomicInteger(0);

    private final Map<Integer, Meal> mealMap = new ConcurrentHashMap<>();

    private int getId() {
        return id.getAndIncrement();
    }

    @Override
    public Meal create(Meal meal) {
        int id = getId();
        meal.setId(id);
        mealMap.put(id, meal);
        log.debug("New meal is saved to database");
        return meal;
    }

    @Override
    public Meal getById(int id) {
        log.debug("Meal with id=" + id + " is received from database");
        return mealMap.get(id);
    }

    @Override
    public List<Meal> getAll() {
        log.debug("All meals are received from database");
        return new ArrayList<>(mealMap.values());
    }

    @Override
    public Meal update(Meal meal) {
        mealMap.replace(meal.getId(), meal);
        log.debug("Meal with id=" + id + " is updated");
        return meal;
    }

    @Override
    public void delete(int id) {
        mealMap.remove(id);
        log.debug("Meal with id=" + id + " is deleted");
    }
}
