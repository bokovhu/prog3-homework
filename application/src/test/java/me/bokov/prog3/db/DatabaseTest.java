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

package me.bokov.prog3.db;

import me.bokov.prog3.Application;
import me.bokov.prog3.TestHelper;
import me.bokov.prog3.service.Database;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseTest {

    @Before
    public void afterEachTest() throws Exception {

        TestHelper.startApplicationInTempEnv(new String[]{"--no-gui"});

    }

    @Test
    public void test_whenInitialized_thenIsRunningShouldReturnTrue() throws Exception {

        assertTrue(Application.getInstance().getWeldContainer().select(Database.class).get().isRunning());

    }

    @Test
    public void test_whenStopped_thenIsRunningShouldReturnFalse() throws Exception {

        Application.getInstance().getWeldContainer().select(Database.class).get().stop();

        assertFalse(Application.getInstance().getWeldContainer().select(Database.class).get().isRunning());

    }

    @Test
    public void test_whenInitialized_tableForChatUserDaoShouldExist() throws Exception {

        Database database = Application.getInstance().getWeldContainer().select(Database.class).get();

        assertTrue(database.getChatUserDao().isTableExists());
        assertTrue(database.getChatRoomDao().isTableExists());
        assertTrue(database.getChatRoomMembershipDao().isTableExists());

    }

}