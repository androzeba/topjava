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

        mealsTo = filteredByCyclesOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();

        mealsTo = filteredByStreamsOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime,
                                                            LocalTime endTime,
                                                            int caloriesPerDay) {
        Map<LocalDate, Integer> allCaloriesPerDay = new HashMap<>();
        meals.forEach(userMeal -> allCaloriesPerDay.merge(getDate(userMeal), userMeal.getCalories(), Integer::sum));
        List<UserMealWithExcess> resultList = new ArrayList<>();
        meals.forEach(userMeal -> {
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                boolean isExcess = allCaloriesPerDay.get(getDate(userMeal)) > caloriesPerDay;
                resultList.add(userMealWithExcess(userMeal, isExcess));
            }
        });
        return resultList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals,
                                                             LocalTime startTime,
                                                             LocalTime endTime,
                                                             int caloriesPerDay) {
        Map<LocalDate, Integer> allCaloriesPerDay = meals.stream()
                .collect(Collectors
                        .toMap(UserMealsUtil::getDate, UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime))
                .map(userMeal -> {
                    boolean isExcess = (allCaloriesPerDay.get(getDate(userMeal)) > caloriesPerDay);
                    return userMealWithExcess(userMeal, isExcess);
                })
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCyclesOptional2(List<UserMeal> meals,
                                                                     LocalTime startTime,
                                                                     LocalTime endTime,
                                                                     int caloriesPerDay) {
        List<UserMealWithExcess> resultList = new ArrayList<>();
        Map<LocalDate, Integer> caloriesMap = new HashMap<>();
        recursionFilter(meals, startTime, endTime, caloriesPerDay, 0, resultList, caloriesMap);
        return resultList;
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional2(List<UserMeal> meals,
                                                                      LocalTime startTime,
                                                                      LocalTime endTime,
                                                                      int caloriesPerDay) {
        return meals.stream().collect(Collector.of(
                () -> {
                    class Aggregator {
                        private final Map<LocalDate, Integer> caloriesMap = new HashMap<>();
                        private final Map<LocalDate, List<UserMealWithExcess>> mealsWithExcess = new HashMap<>();
                        private final Map<LocalDate, List<UserMealWithExcess>> mealsWithoutExcess = new HashMap<>();
                    }
                    return new Aggregator();
                },
                (aggregator, userMeal) -> {
                    LocalDate date = getDate(userMeal);
                    aggregator.caloriesMap.merge(date, userMeal.getCalories(), Integer::sum);
                    boolean isExcess = aggregator.caloriesMap.get(date) > caloriesPerDay;
                    if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                        aggregator.mealsWithExcess.computeIfAbsent(date, d -> new ArrayList<>())
                                .add(userMealWithExcess(userMeal, true));
                        if (!isExcess) {
                            aggregator.mealsWithoutExcess.computeIfAbsent(date, d -> new ArrayList<>())
                                    .add(userMealWithExcess(userMeal, false));
                        }
                    }
                    if (isExcess) {
                        aggregator.mealsWithoutExcess.remove(date);
                    }
                },
                (aggregator1, aggregator2) -> {
                    aggregator2.mealsWithExcess.forEach((key, value) -> aggregator1.mealsWithExcess
                            .computeIfAbsent(key, k -> new ArrayList<>()).addAll(value));
                    aggregator2.mealsWithoutExcess.forEach((key, value) -> aggregator1.mealsWithoutExcess
                            .computeIfAbsent(key, k -> new ArrayList<>()).addAll(value));
                    return aggregator1;
                },
                aggregator -> {
                    aggregator.mealsWithExcess.putAll(aggregator.mealsWithoutExcess);
                    return aggregator.mealsWithExcess.values().stream()
                            .flatMap(List::stream)
                            .collect(Collectors.toList());
                }
                )
        );
    }

    private static LocalTime getTime(UserMeal userMeal) {
        return userMeal.getDateTime().toLocalTime();
    }

    private static LocalDate getDate(UserMeal userMeal) {
        return userMeal.getDateTime().toLocalDate();
    }

    private static UserMealWithExcess userMealWithExcess(UserMeal userMeal, boolean isExcess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), isExcess);
    }

    private static void recursionFilter(List<UserMeal> meals,
                                        LocalTime startTime,
                                        LocalTime endTime,
                                        int caloriesPerDay,
                                        int index,
                                        List<UserMealWithExcess> resultList,
                                        Map<LocalDate, Integer> caloriesMap) {
        if (meals.size() > 0) {
            UserMeal userMeal = meals.get(index);
            caloriesMap.merge(getDate(userMeal), userMeal.getCalories(), Integer::sum);
            if (index < meals.size() - 1) {
                recursionFilter(meals, startTime, endTime, caloriesPerDay, index + 1, resultList, caloriesMap);
            }
            boolean isExcess = caloriesMap.get(getDate(userMeal)) > caloriesPerDay;
            if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                resultList.add(userMealWithExcess(userMeal, isExcess));
            }
        }
    }
}