package ru.javawebinar.topjava;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class TimeMeasureRule implements TestRule {
    private static final Logger LOGGER = LoggerFactory.getLogger("ru.javawebinar.topjava");

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Instant start = Instant.now();
                base.evaluate();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                String msg = description.getMethodName() + " Test time = " + timeElapsed + " ms";
                LOGGER.info(msg);
                MealTestData.TEST_TIME.put(description, timeElapsed);
            }
        };
    }
}