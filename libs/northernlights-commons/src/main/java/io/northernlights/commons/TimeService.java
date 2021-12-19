package io.northernlights.commons;

import java.time.ZonedDateTime;

@FunctionalInterface
public interface TimeService {

    ZonedDateTime now();
}
