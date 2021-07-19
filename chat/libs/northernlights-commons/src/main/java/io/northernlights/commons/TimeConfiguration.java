package io.northernlights.commons;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Configuration
public class TimeConfiguration {

    private static final ZoneId ZONE_ID = ZoneOffset.UTC;
    private static final Clock CLOCK = Clock.system(ZONE_ID);

    @Bean
    public TimeService timeService() {
        return () -> ZonedDateTime.now(CLOCK);
    }

}
