package io.northernlights.commons;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeServiceTest {

    @Test
    void now_should_return_configured_clock_zoned_data_time() {
        // Given
        ZoneId ZONE_ID = ZoneOffset.UTC;
        Clock CLOCK = Clock.system(ZONE_ID);
        TimeService timeService = () -> ZonedDateTime.now(CLOCK);

        // When
        ZonedDateTime now = timeService.now();

        // Then
        assertThat(now).isNotNull();
    }
}
