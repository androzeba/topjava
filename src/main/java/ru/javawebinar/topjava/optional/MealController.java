package ru.javawebinar.topjava.optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Objects;

public class MealController extends HttpServlet {
    private final MealDao mealDao = new MealDaoImpl();
    private static final Logger log = LoggerFactory.getLogger(MealController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (Objects.nonNull(action)) {
            if (action.equalsIgnoreCase("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                log.debug("Request to delete meal with id=" + id);
                mealDao.delete(id);
            }
            if (action.equalsIgnoreCase("update")) {
                request.setAttribute("action", "update");
                int id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("mealToUpdate", mealDao.readById(id));
                log.debug("Request to update meal with id=" + id);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
            }
            if (action.equalsIgnoreCase("create")) {
                request.setAttribute("action", "create");
                log.debug("Request to create new meal");
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
            }
        }
        log.debug("Request to output all meals");
        List<MealTo> mealsToList = MealsUtil.filteredByStreams(mealDao.readAll(), LocalTime.MIN, LocalTime.MAX, 2000);
        request.setAttribute("mealsToList", mealsToList);
        request.setAttribute("action", "read");
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String dateParameter = request.getParameter("date");
        String timeParameter = request.getParameter("time");
        String description = request.getParameter("description");
        String caloriesParameter = request.getParameter("calories");
        int calories;
        LocalDate date;
        LocalTime time;
        if (action != null && !action.equals("")) {
            if (action.equalsIgnoreCase("update")) {
                log.debug("Enter to update meal block");
                int id = Integer.parseInt(request.getParameter("id"));
                Meal meal = mealDao.readById(id);
                if (dateParameter == null || dateParameter.equals("")) {
                    date = meal.getDate();
                } else {
                    try {
                        date = LocalDate.parse(dateParameter);
                    } catch (Exception e) {
                        response.sendRedirect("meals");
                        return;
                    }
                }
                if (timeParameter == null || timeParameter.equals("")) {
                    time = meal.getTime();
                } else {
                    try {
                        time = LocalTime.parse(timeParameter);
                    } catch (Exception e) {
                        response.sendRedirect("meals");
                        return;
                    }
                }
                if (description == null || description.equals("")) {
                    description = meal.getDescription();
                }
                if (caloriesParameter == null || caloriesParameter.equals("")) {
                    calories = meal.getCalories();
                } else {
                    try {
                        calories = Integer.parseInt(caloriesParameter);
                    } catch (NumberFormatException e) {
                        response.sendRedirect("meals");
                        return;
                    }
                }
                mealDao.update(id, LocalDateTime.of(date, time), description, calories);
            }
            if (action.equalsIgnoreCase("create")) {
                log.debug("Enter to create meal block");
                if (dateParameter != null && !dateParameter.equals("")
                        && timeParameter != null && !timeParameter.equals("")
                        && description != null && !description.equals("")
                        && caloriesParameter != null && !caloriesParameter.equals("")) {
                    try {
                        calories = Integer.parseInt(caloriesParameter);
                        date = LocalDate.parse(dateParameter);
                        time = LocalTime.parse(timeParameter);
                    } catch (Exception e) {
                        response.sendRedirect("meals");
                        return;
                    }
                    mealDao.create(LocalDateTime.of(date, time), description, calories);
                }
            }
        }
        response.sendRedirect("meals");
    }
}
