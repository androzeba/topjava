package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping("/update")
    public String preCreate(HttpServletRequest request) {
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        request.setAttribute("meal", meal);
        return "mealForm";
    }

    @PostMapping("/update")
    public String create(HttpServletRequest request) {
        Assert.notNull(create(newMeal(request)), "unsuccessful creation");
        return "redirect:/meals";
    }

    @GetMapping("/update/{id}")
    public String preUpdate(@PathVariable("id") int id, HttpServletRequest request) {
        request.setAttribute("meal", get(id));
        return "mealForm";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") int id, HttpServletRequest request) {
        update(newMeal(request), id);
        return "redirect:/meals";
    }

    @GetMapping("/delete/{id}")
    public String del(@PathVariable("id") int id) {
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping("")
    public String getAll(HttpServletRequest request) {
        request.setAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping("/filter")
    public String getAllFiltered(HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        request.setAttribute("meals", getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    private Meal newMeal(HttpServletRequest request) {
        return new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
    }
}
