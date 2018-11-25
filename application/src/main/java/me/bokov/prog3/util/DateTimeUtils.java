/*
 *     Chatter - my Programming III. homework assignment
 *     Copyright (C) 2018  Botond János Kovács
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.bokov.prog3.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

public final class DateTimeUtils {

    public static int differenceInDays (Date date1, Date date2) {

        Date smaller = date1.before(date2) ? date1 : date2;
        Date bigger = date1.after(date2) ? date1 : date2;

        Instant i1 = smaller.toInstant();
        Instant i2 = bigger.toInstant();

        return (int) Math.abs(i1.atOffset(ZoneOffset.UTC).toLocalDate().toEpochDay() - i2.atOffset(ZoneOffset.UTC).toLocalDate().toEpochDay());

    }

}
