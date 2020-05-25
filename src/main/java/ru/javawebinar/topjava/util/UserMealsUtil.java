package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();

        mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();

        mealsTo = filteredByStreamsOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> allCaloriesPerDay = new HashMap<>();
        List<UserMealWithExcess> resultList = new ArrayList<>();
        meals.forEach(userMeal -> allCaloriesPerDay.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum));
        meals.forEach(userMeal -> {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean isExcess = allCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay;
                resultList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), isExcess));
            }
        });
        return resultList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> allCaloriesPerDay = new HashMap<>();
        List<UserMeal> filteredList = meals.stream()
                .peek(userMeal -> allCaloriesPerDay.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum))
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
        return filteredList.stream().map(userMeal -> {
            boolean isExcess = (allCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay);
            return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), isExcess);
        }).collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(Collector.of(
                () -> {
                    List<Object> list = new ArrayList<>();
                    list.add(new HashMap<LocalDate, Integer>());
                    list.add(new HashMap<LocalDate, List<UserMeal>>());
                    list.add(new HashMap<LocalDate, List<UserMealWithExcess>>());
                    list.add(new HashMap<LocalDate, List<UserMealWithExcess>>());
                    return list;
                },

                (list, userMeal) -> {
                    LocalDate date = userMeal.getDateTime().toLocalDate();
                    Map<LocalDate, Integer> caloriesMap = (HashMap<LocalDate, Integer>) list.get(0);
                    Map<LocalDate, List<UserMeal>> filteredUserMeal = (HashMap<LocalDate, List<UserMeal>>) list.get(1);
                    Map<LocalDate, List<UserMealWithExcess>> mealsWithExcess = (HashMap<LocalDate, List<UserMealWithExcess>>) list.get(2);
                    Map<LocalDate, List<UserMealWithExcess>> mealsWithoutExcess = (HashMap<LocalDate, List<UserMealWithExcess>>) list.get(3);
                    List<UserMeal> mealsOnDate = filteredUserMeal.getOrDefault(date, new ArrayList<>());
                    List<UserMealWithExcess> mealsWithExcessOnDate = mealsWithExcess.getOrDefault(date, new ArrayList<>());
                    List<UserMealWithExcess> mealsWithoutExcessOnDate = mealsWithoutExcess.getOrDefault(date, new ArrayList<>());
                    caloriesMap.merge(date, userMeal.getCalories(), Integer::sum);

                    if (caloriesMap.get(date) <= caloriesPerDay) {
                        if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                            mealsOnDate.add(userMeal);
                            filteredUserMeal.put(date, mealsOnDate);
                            mealsWithoutExcessOnDate.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false));
                            mealsWithoutExcess.put(date, mealsWithoutExcessOnDate);
                        }
                    } else {
                        if (mealsWithoutExcessOnDate.size() > 0) {
                            mealsWithExcessOnDate = mealsOnDate.stream()
                                    .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), true))
                                    .collect(Collectors.toList());
                            mealsWithExcess.put(date, mealsWithExcessOnDate);
                            mealsWithoutExcess.remove(date);
                        }
                        if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                            mealsOnDate.add(userMeal);
                            filteredUserMeal.put(date, mealsOnDate);
                            mealsWithExcessOnDate.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true));
                            mealsWithExcess.put(date, mealsWithExcessOnDate);
                        }
                    }
                    list.set(0, caloriesMap);
                    list.set(1, filteredUserMeal);
                    list.set(2, mealsWithExcess);
                    list.set(3, mealsWithoutExcess);
                },

                (list1, list2) -> list1,

                list -> {
                    Map<LocalDate, List<UserMealWithExcess>> mealsWithExcess = (HashMap<LocalDate, List<UserMealWithExcess>>) list.get(2);
                    Map<LocalDate, List<UserMealWithExcess>> mealsWithoutExcess = (HashMap<LocalDate, List<UserMealWithExcess>>) list.get(3);
                    return Stream.concat(mealsWithExcess.values().stream(), mealsWithoutExcess.values().stream())
                            .flatMap(List::stream).collect(Collectors.toList());
                }
                )
        );
    }
}