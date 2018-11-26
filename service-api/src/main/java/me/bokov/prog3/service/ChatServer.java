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

package me.bokov.prog3.service;

import me.bokov.prog3.command.endpoint.ChatClientEndpoint;
import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.server.ServerConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Interface for chat server management
 */
public interface ChatServer {

    /**
     * Starts the chat server using the supplied server configuration
     * @param configuration the server configuration
     * @throws IllegalStateException if the server is already running
     */
    void start(ServerConfiguration configuration);

    /**
     * Checks whether the chat server is currently running
     * @return true, if the chat server is running, false otherwise
     */
    boolean isRunning();

    /**
     * Retrieves the server configuration that was used to start the server
     * @return the server configuration of the currently running chat server
     */
    ServerConfiguration getServerConfiguration();

    /**
     * Stops the currently running chat server, if it's running
     * @throws IllegalStateException if the server is not running
     */
    void stop();

    /**
     * Retrieves a list of the currently connected chat users
     * @return the list of the connected users
     */
    List<ChatUserVO> getConnectedUsers();

    /**
     * Retrieves the endpoints for the currently connected chat users, that are members of a given room
     * @param roomId the ID of the room
     * @return a list of the client endpoints
     */
    List <ChatClientEndpoint> clientsInRoom (Long roomId);

    /**
     * Retrieves (if connected) the endpoint of a chat user
     * @param userId the ID of the user
     * @return an {@link Optional}, that contains the endpoint if the user is connected, or an empty {@link Optional} if
     * the user is currently offline
     */
    Optional <ChatClientEndpoint> clientByUserId (Long userId);

    /**
     * Bans a given user from the server
     * @param userId the ID of the user to ban
     */
    void banUser (Long userId);

    /**
     * Unbans a given user from the server
     * @param userId the ID of the user to unban
     */
    void unbanUser (Long userId);

}
