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
        Map<LocalDate, Integer> caloriesMap = new HashMap<>();
        Map<LocalDate, List<UserMealWithExcess>> mealWithExcess = new HashMap<>();
        Map<LocalDate, List<UserMealWithExcess>> mealWithoutExcess = new HashMap<>();
        Set<UserMealWithExcess> mealNoExcessList = new HashSet<>();
        Set<UserMealWithExcess> exclusionList = new HashSet<>();
        List<UserMealWithExcess> resultList = new ArrayList<>();
        meals.forEach(userMeal -> {
            LocalDate date = getDate(userMeal);
            caloriesMap.merge(date, userMeal.getCalories(), Integer::sum);
            if (caloriesMap.get(date) <= caloriesPerDay) {
                if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                    mealWithExcess.merge(date,
                            new ArrayList<>(Collections.singletonList(userMealWithExcess(userMeal, true))),
                            (value1, value2) -> {
                                value1.addAll(value2);
                                return value1;
                            });
                    UserMealWithExcess user = userMealWithExcess(userMeal, false);
                    mealWithoutExcess.merge(date,
                            new ArrayList<>(Collections.singletonList(user)),
                            (value1, value2) -> {
                                value1.addAll(value2);
                                return value1;
                            });
                    mealNoExcessList.add(user);
                }
            } else {
                if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                    resultList.add(userMealWithExcess(userMeal, true));
                }
                if (mealWithoutExcess.get(date).size() > 0) {
                    resultList.addAll(mealWithExcess.get(date));
                    exclusionList.addAll(mealWithoutExcess.get(date));
                    mealWithoutExcess.remove(date);
                }
            }
        });
        mealNoExcessList.removeAll(exclusionList);
        resultList.addAll(mealNoExcessList);
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
                        private final Map<LocalDate, List<UserMealWithExcess>> mealWithExcess = new HashMap<>();
                        private final Map<LocalDate, List<UserMealWithExcess>> mealWithoutExcess = new HashMap<>();
                    }
                    return new Aggregator();
                },
                (aggregator, userMeal) -> {
                    LocalDate date = getDate(userMeal);
                    aggregator.caloriesMap.merge(date, userMeal.getCalories(), Integer::sum);
                    if (aggregator.caloriesMap.get(date) <= caloriesPerDay) {
                        if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                            aggregator.mealWithExcess.merge(date,
                                    new ArrayList<>(Collections.singletonList(userMealWithExcess(userMeal, true))),
                                    (value1, value2) -> {
                                        value1.addAll(value2);
                                        return value1;
                                    });
                            aggregator.mealWithoutExcess.merge(date,
                                    new ArrayList<>(Collections.singletonList(userMealWithExcess(userMeal, false))),
                                    (value1, value2) -> {
                                        value1.addAll(value2);
                                        return value1;
                                    });
                        }
                    } else {
                        if (TimeUtil.isBetweenHalfOpen(getTime(userMeal), startTime, endTime)) {
                            aggregator.mealWithExcess.merge(date,
                                    new ArrayList<>(Collections.singletonList(userMealWithExcess(userMeal, true))),
                                    (value1, value2) -> {
                                        value1.addAll(value2);
                                        return value1;
                                    });
                        }
                        aggregator.mealWithoutExcess.remove(date);
                    }
                },
                (aggregator1, aggregator2) -> {
                    aggregator2.mealWithExcess.forEach((key, value) -> aggregator1.mealWithExcess
                            .merge(key, value, (v1, v2) -> {
                                v1.addAll(v2);
                                return v1;
                            }));
                    aggregator2.mealWithoutExcess.forEach((key, value) -> aggregator1.mealWithoutExcess
                            .merge(key, value, (v1, v2) -> {
                                v1.addAll(v2);
                                return v1;
                            }));
                    return aggregator1;
                },
                aggregator -> Stream.concat(aggregator.mealWithoutExcess.entrySet().stream(),
                        aggregator.mealWithExcess.entrySet().stream()
                                .filter(entry -> !aggregator.mealWithoutExcess.containsKey(entry.getKey())))
                        .map(Map.Entry::getValue)
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
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
}