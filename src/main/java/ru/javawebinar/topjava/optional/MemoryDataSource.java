package ru.javawebinar.topjava.optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryDataSource {
    private static volatile MemoryDataSource instance;

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
    }

    private MemoryDataSource() {
    }

    public static MemoryDataSource getInstance() {
        if (instance == null) {
            synchronized (MemoryDataSource.class) {
                if (instance == null) {
                    instance = new MemoryDataSource();
                }
            }
        }
        return instance;
    }

    public static int getID() {
        return ID.getAndIncrement();
    }

    public Meal getMeal(int id) {
        if (id >= 0 && id < mealsList.size()) {
            log.debug("Meal with id=" + id + " is received from database");
            return mealsList.get(id);
        }
        return null;
    }

    public List<Meal> getAllMeals() {
        log.debug("All meals are received from database");
        return mealsList;
    }

    public void addMeal(LocalDateTime dateTime, String description, int calories) {
        mealsList.add(new Meal(dateTime, description, calories));
        log.debug("New meal is saved to database");
    }

    public void updateMeal(int id, LocalDateTime dateTime, String description, int calories) {
        if (id >= 0 && id < mealsList.size() && mealsList.get(id) != null) {
            Meal meal = mealsList.get(id);
            meal.setDateTime(dateTime);
            meal.setDescription(description);
            meal.setCalories(calories);
            mealsList.set(id, meal);
            log.debug("Meal with id=" + id + " is updated");
        }
    }

    public void deleteMeal(int id) {
        if (id >= 0 && id < mealsList.size()) {
            mealsList.set(id, null);
            log.debug("Meal with id=" + id + " is deleted");
        }
    }
}
