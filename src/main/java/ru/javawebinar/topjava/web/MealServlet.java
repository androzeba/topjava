package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.dao.*;
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
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class MealServlet extends HttpServlet {

    public static final int MAX_CALORIES = 2000;

    private MealDao mealDao;

    private static Logger log;

    private static DateTimeFormatter formatter;

    @Override
    public void init() throws ServletException {
        mealDao = new MemoryMapMealDao();
//        mealDao = new MemoryListMealDao();
        log = LoggerFactory.getLogger(MealServlet.class);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        mealDao.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

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
                    request.setAttribute("mealToUpdate", new MealTo(mealDao.getById(id), false));
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
        List<MealTo> mealsToList = MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, MAX_CALORIES);
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
            Meal meal = new Meal(id, LocalDateTime.of(date, time), description, calories);
            mealDao.update(meal);
        }
        if (action.equalsIgnoreCase("create")) {
            log.debug("Enter to create meal block");
            mealDao.create(new Meal(LocalDateTime.of(date, time), description, calories));
        }
        response.sendRedirect("meals");
    }
}
