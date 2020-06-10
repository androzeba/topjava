package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemoryListMealDao implements MealDao {

    private static final Logger log = LoggerFactory.getLogger(MemoryListMealDao.class);

    private final AtomicInteger id = new AtomicInteger(0);

    private final List<Meal> mealsList = new CopyOnWriteArrayList<>();

    private int getId() {
        return id.getAndIncrement();
    }

    @Override
    public Meal create(Meal meal) {
        meal.setId(getId());
        mealsList.add(meal);
        log.debug("New meal is saved to database");
        return meal;
    }

    @Override
    public Meal getById(int id) {
        if (id >= 0 && id < mealsList.size()) {
            log.debug("Meal with id=" + id + " is received from database");
            return mealsList.get(id);
        }
        return null;
    }

    @Override
    public List<Meal> getAll() {
        List<Meal> result = mealsList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.debug("All meals are received from database");
        return result;
    }

    @Override
    public Meal update(Meal meal) {
        int id = meal.getId();
        if (id >= 0 && id < mealsList.size()) {
            mealsList.set(id, meal);
            log.debug("Meal with id=" + id + " is updated");
            return meal;
        }
        return null;
    }

    @Override
    public void delete(int id) {
        if (id >= 0 && id < mealsList.size()) {
            mealsList.set(id, null);
            log.debug("Meal with id=" + id + " is deleted");
        }
    }
}
