package io.northernlights.commons;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;

@Configuration
public class TimeConfiguration {

    private static final ZoneId ZONE_ID = ZoneOffset.UTC;
    private static final Clock CLOCK = Clock.system(ZONE_ID);

    @PostConstruct
    public void setUpDefaultZoneId() {
        setDefault(getTimeZone(ZONE_ID));
    }

    @Bean
    public TimeService timeService() {
        return () -> ZonedDateTime.now(CLOCK);
    }

}
