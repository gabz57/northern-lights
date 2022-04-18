package io.northernlights.commons;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;

@TestConfiguration
public class TestableTimeConfiguration {

    @Bean
    public TimeService timeService() {
        return testTimeService();
    }

    @Bean
    public TestTimeService testTimeService() {
        return new TestTimeService();
    }

    public static class TestTimeService implements TimeService {

        private static final ZoneId ZONE_ID = ZoneOffset.UTC;
        private static final Clock CLOCK = Clock.system(ZONE_ID);

        private ZonedDateTime now;

        private TestTimeService() {
            this.now = ZonedDateTime.now(CLOCK);
            setDefault(getTimeZone(ZONE_ID));
        }

        public ZonedDateTime now() {
            return now;
        }

        public void setNow(ZonedDateTime now) {
            this.now = now;
        }
    }
}
