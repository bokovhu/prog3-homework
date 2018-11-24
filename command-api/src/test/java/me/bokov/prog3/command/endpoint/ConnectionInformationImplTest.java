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

package me.bokov.prog3.command.endpoint;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionInformationImplTest {

    @Test
    public void test_getters() {

        ConnectionInformationImpl ci = new ConnectionInformationImpl(
                123,
                456,
                "1.2.3.4",
                "5.6.7.8"
        );

        assertEquals(123, ci.getLocalPort());
        assertEquals(456, ci.getRemotePort());
        assertEquals("1.2.3.4", ci.getLocalAddress());
        assertEquals("5.6.7.8", ci.getRemoteAddress());

    }

    @Test
    public void test_toString() {

        ConnectionInformationImpl ci = new ConnectionInformationImpl(
                123,
                456,
                "1.2.3.4",
                "5.6.7.8"
        );

        assertNotNull(ci.toString());

    }

}