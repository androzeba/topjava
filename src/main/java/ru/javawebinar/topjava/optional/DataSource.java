package ru.javawebinar.topjava.optional;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DataSource {
    private static volatile DataSource instance;

    public static final AtomicInteger ID = new AtomicInteger(0);

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

    private DataSource() {
    }

    public static DataSource getInstance() {
        if (instance == null) {
            synchronized (DataSource.class) {
                if (instance == null) {
                    instance = new DataSource();
                }
            }
        }
        return instance;
    }

    public static List<Meal> getMealsList() {
        return mealsList;
    }

    public Meal getMeal(int id) {
        if (id < mealsList.size()) {
            return mealsList.get(id);
        }
        return null;
    }

    public List<Meal> getAllMeals() {
        return mealsList;
    }

    public void addMeal(LocalDateTime dateTime, String description, int calories) {
        mealsList.add(new Meal(dateTime, description, calories));
    }

    public void updateMeal(int id, LocalDateTime dateTime, String description, int calories) {
        if (id < mealsList.size() && mealsList.get(id) != null) {
            Meal meal = mealsList.get(id);
            meal.setDateTime(dateTime);
            meal.setDescription(description);
            meal.setCalories(calories);
            mealsList.set(id, meal);
        }
    }

    public void deleteMeal(int id) {
        if (id < mealsList.size()) {
            mealsList.set(id, null);
        }
    }
}
