package ru.javawebinar.topjava.optional;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public class MealDaoImpl implements MealDao {

    private DataSource dataSource = DataSource.getInstance();

    @Override
    public void create(LocalDateTime dateTime, String description, int calories) {
        dataSource.addMeal(dateTime, description, calories);
    }

    @Override
    public Meal readById(int id) {
        return dataSource.getMeal(id);
    }

    @Override
    public List<Meal> readAll() {
        return dataSource.getAllMeals();
    }

    @Override
    public void update(int id, LocalDateTime dateTime, String description, int calories) {
        dataSource.updateMeal(id, dateTime, description, calories);
    }

    @Override
    public void delete(int id) {
        dataSource.deleteMeal(id);
    }
}
