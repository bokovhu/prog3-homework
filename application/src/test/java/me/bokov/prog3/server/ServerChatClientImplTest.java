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

package me.bokov.prog3.server;

import me.bokov.prog3.Application;
import me.bokov.prog3.TestHelper;
import me.bokov.prog3.client.ChatClientImpl;
import me.bokov.prog3.service.client.ConnectionConfiguration;
import me.bokov.prog3.service.server.ServerChatClient;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerChatClientImplTest {

    @Test
    public void test_sessionCapabilities () {

        ServerChatClient ccc = new ServerChatClientImpl(null);

        assertFalse(ccc.isSessionValueSet("test-key"));
        ccc.setSessionValue("test-key", "test-value");
        assertTrue(ccc.isSessionValueSet("test-key"));
        assertEquals("test-value", ccc.getSessionValue("test-key"));
        ccc.removeSessionValue("test-key");
        assertFalse(ccc.isSessionValueSet("test-key"));
        ccc.setSessionValue("test-key-1", "test-value-1");
        ccc.setSessionValue("test-key-2", "test-value-2");
        ccc.clearSession();
        assertFalse(ccc.isSessionValueSet("test-key-1"));
        assertFalse(ccc.isSessionValueSet("test-key-2"));

    }

    @Test
    public void test () throws Exception {

        // Start the application in server mode
        TestHelper.startApplicationInTempEnv(new String[] { "--server", "--port", "10467" });
        ChatServerImpl chatServer = Application.getInstance().getWeldContainer().select(ChatServerImpl.class).get();
        Thread.sleep(1000L);

        // Connect to the server
        ChatClientImpl chatClient = Application.getInstance().getWeldContainer().select(ChatClientImpl.class).get();
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setHost("localhost");
        connectionConfiguration.setPort(10467);
        connectionConfiguration.setUsername("Test");
        chatClient.connect(connectionConfiguration);

        // ServerChatClient should be in the server
        assertFalse(chatServer.getServerChatClients().isEmpty());
        ServerChatClientImpl serverChatClient = (ServerChatClientImpl) chatServer.getServerChatClients().get(0);
        assertTrue(serverChatClient.isSessionValueSet("username"));
        assertEquals("Test", serverChatClient.getSessionValue("username"));

        // Now, disconnect the client
        chatClient.disconnect();
        Thread.sleep(2000L);
        assertTrue(chatServer.getServerChatClients().isEmpty());

        // And finally, destroy the application
        Application.destroyInstance();

    }

}