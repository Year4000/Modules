/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.infractions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class TimeDuration {
    private final TimeUnit unit;
    private final long time;
    @Getter
    private final boolean infinite;

    /** Return the time duration in secs */
    public int toSecs() {
        return (int) TimeUnit.SECONDS.convert(time, unit);
    }

    public static TimeDuration getFromString(String value) {
        boolean forever = value.equals("-1");

        value = value.contains("m") || value.contains("s") || value.contains("h") || value.contains("d") ? value : value + "m";

        return new TimeDuration(TimeUnit.MILLISECONDS, forever ? Integer.MAX_VALUE : Duration.parse("PT" + value.toUpperCase()).toMillis(), forever);
    }
}
