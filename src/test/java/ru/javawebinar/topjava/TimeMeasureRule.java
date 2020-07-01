package ru.javawebinar.topjava;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.service.MealServiceTest;

import java.time.Duration;
import java.time.Instant;

public class TimeMeasureRule extends TestWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger("timeMeasure");
    private Instant start;

    @Override
    protected void starting(Description description) {
        start = Instant.now();
    }

    @Override
    protected void finished(Description description) {
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        String msg = description.getMethodName() + " Test time = " + timeElapsed + " ms";
        LOGGER.info(msg);
        MealServiceTest.testTime.put(description, timeElapsed);
    }
}