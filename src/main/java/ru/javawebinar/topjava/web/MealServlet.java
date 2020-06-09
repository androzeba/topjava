package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MemoryDataSource;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private final MealDao mealDao = new MemoryDataSource();
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (Objects.nonNull(action)) {
            switch (action) {
                case "delete": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    log.debug("Request to delete meal with id=" + id);
                    mealDao.delete(id);
                    response.sendRedirect("meals");
                    return;
                }
                case "update": {
                    request.setAttribute("action", "update");
                    int id = Integer.parseInt(request.getParameter("id"));
                    log.debug("Request to update meal with id=" + id);
                    request.setAttribute("mealToUpdate", mealDao.getById(id));
                    request.setAttribute("formatter", formatter);
                    request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                    return;
                }
                case "create": {
                    log.debug("Request to create new meal");
                    request.setAttribute("action", "create");
                    request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                    return;
                }
            }
        }
        log.debug("Request to output all meals");
        List<MealTo> mealsToList = MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, 2000);
        request.setAttribute("mealsToList", mealsToList);
        request.setAttribute("formatter", formatter);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        LocalDate date = LocalDate.parse(request.getParameter("date"));
        LocalTime time = LocalTime.parse(request.getParameter("time"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        if (action.equalsIgnoreCase("update")) {
            log.debug("Enter to update meal block");
            int id = Integer.parseInt(request.getParameter("id"));
            Meal meal = mealDao.getById(id);
            meal.setDateTime(LocalDateTime.of(date, time));
            meal.setDescription(description);
            meal.setCalories(calories);
            mealDao.update(meal);
        }
        if (action.equalsIgnoreCase("create")) {
            log.debug("Enter to create meal block");
            mealDao.create(new Meal(LocalDateTime.of(date, time), description, calories));
        }
        response.sendRedirect("meals");
    }
}
