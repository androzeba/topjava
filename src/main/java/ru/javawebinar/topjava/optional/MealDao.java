package ru.javawebinar.topjava.optional;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealDao {

    void create(LocalDateTime dateTime, String description, int calories);

    Meal readById(int id);

    List<Meal> readAll();

    void update(int id, LocalDateTime dateTime, String description, int calories);

    void delete(int id);
}
