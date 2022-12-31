/*
 * Copyright (C) 2019  OopsieWoopsie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.tini.announcer.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;

    private static final long SECOND = 1000; // millis
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    // not every month has 30 days, and 365 / 12 = 30.4~
    // so i will not do the month constant
    private static final long YEAR = DAY * 365;

    public static String parseMillis(long millis) {
        if (millis >= YEAR) {
            return UNIT.toDays(millis) / 365 + " year(s)";
        }
        else if (millis >= DAY) {
            return UNIT.toDays(millis) + " day(s)";
        }
        else if (millis >= HOUR) {
            return UNIT.toHours(millis) + " hour(s)";
        }
        else if (millis >= MINUTE) {
            return UNIT.toMinutes(millis) + " minute(s)";
        }
        else if (millis >= SECOND) {
            return UNIT.toSeconds(millis) + " second(s)";
        }
        return "unknown";
    }
}
