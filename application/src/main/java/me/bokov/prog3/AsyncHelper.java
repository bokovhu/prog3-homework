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

package me.bokov.prog3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This utility class can be used to execute small tasks in an async manner (in a different {@link Thread} than the
 * caller's)
 */
public final class AsyncHelper {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * This method takes a single Runnable and executes it on a different thread (in an ExecutorService) than the
     * caller's thread
     * @param r the Runnable to execute
     */
    public static void runAsync (Runnable r) {
        EXECUTOR_SERVICE.submit(r);
    }

    static void destroy () {

        EXECUTOR_SERVICE.shutdownNow();
        try {
            EXECUTOR_SERVICE.awaitTermination(60000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
