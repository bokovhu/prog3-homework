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

package me.bokov.prog3.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandExceptionTest {

    @Test
    public void test_emptyConstructor() {

        try {

            throw new CommandException();

        } catch (CommandException ex) {

            assertNull(ex.getMessage());
            assertNull(ex.getCause());

        } catch (Throwable th) {
            assertFalse(true);
        }

    }

    @Test
    public void test_messageOnlyConstructor() {

        try {

            throw new CommandException("My test message");

        } catch (CommandException ex) {

            assertEquals("My test message", ex.getMessage());
            assertNull(ex.getCause());

        } catch (Throwable th) {
            assertFalse(true);
        }

    }

    @Test
    public void test_messageAndCauseConstructor() {

        try {

            throw new CommandException("My test message", new IllegalArgumentException("Hello"));

        } catch (CommandException ex) {

            assertEquals("My test message", ex.getMessage());
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
            assertEquals("Hello", ex.getCause().getMessage());

        } catch (Throwable th) {
            assertFalse(true);
        }

    }

    @Test
    public void test_causeOnlyConstructor() {

        try {

            throw new CommandException(new IllegalArgumentException("Hello there"));

        } catch (CommandException ex) {

            assertTrue(ex.getCause() instanceof IllegalArgumentException);
            assertEquals("Hello there", ex.getCause().getMessage());

        } catch (Throwable th) {
            assertFalse(true);
        }

    }

}