package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemoryDataSource implements MealDao {

    private static final AtomicInteger ID = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger(MemoryDataSource.class);

    private static final List<Meal> mealsList = new CopyOnWriteArrayList<>();

    static {
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        mealsList.add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        mealsList.forEach(meal -> meal.setId(getID()));
    }

    public static int getID() {
        return ID.getAndIncrement();
    }

    @Override
    public Meal create(Meal meal) {
        meal.setId(getID());
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
        mealsList.set(id, meal);
        log.debug("Meal with id=" + id + " is updated");
        return meal;
    }

    @Override
    public void delete(int id) {
        if (id >= 0 && id < mealsList.size()) {
            mealsList.set(id, null);
            log.debug("Meal with id=" + id + " is deleted");
        }
    }
}
