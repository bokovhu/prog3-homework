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

package me.bokov.prog3.service.common;

import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.command.response.Response;

import java.net.Socket;
import java.util.UUID;

/**
 * Base interface for all services that are capable of network communication
 */
public interface CommunicationCapableService {

    /**
     * Starts the communication on a given socket
     * @param socket the socket to start the communication on
     */
    void start(Socket socket);

    /**
     * Stops the communication service
     */
    void stop();

    /**
     * Sends a given request over the network
     * @param request the request to send
     */
    void send(Request request);

    /**
     * Reads the response to a given message ID, waiting and blocking if necessary while it becomes
     * availble
     * @param messageId the message ID to read
     * @param timeoutInMillisec the read timeout in milliseconds
     * @return the response
     */
    Response readResponse(String messageId, long timeoutInMillisec);

    UUID getId ();

}
