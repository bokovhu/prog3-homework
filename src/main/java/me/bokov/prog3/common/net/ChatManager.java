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

package me.bokov.prog3.common.net;

import me.bokov.prog3.client.net.ChatServerService;
import me.bokov.prog3.server.ChatServer;
import me.bokov.prog3.server.ServerConfig;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatManager {

    private ChatServerService currentRemoteChatServerService = null;
    private ChatServer currentRunningServer = null;

    public ChatServerService connectToServer (String hostname, Integer port) {

        if (currentRemoteChatServerService != null && currentRunningServer != null) {
            throw new IllegalStateException("Cannot connect to another server / start a new server in the same window!");
        }

        throw new UnsupportedOperationException("Not yet implemented!");

    }

    public ChatServer startNewServer (ServerConfig serverConfig) {

        if (currentRemoteChatServerService != null && currentRunningServer != null) {
            throw new IllegalStateException("Cannot connect to another server / start a new server in the same window!");
        }

        currentRunningServer = new ChatServer(serverConfig);
        currentRunningServer.start();
        return currentRunningServer;

    }

    public ChatServerService getCurrentRemoteChatServerService () {
        return currentRemoteChatServerService;
    }

    public ChatServer getCurrentRunningServer () {
        return currentRunningServer;
    }

}
