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

package me.bokov.prog3.command.client;

import me.bokov.prog3.command.Command;

/**
 * Represents a {@code HELLO} command.
 *
 * As part of the authentication flow, the first message of the clients is always a {@code HELLO} message. In this
 * message the client specifies its username. If the user is banned, a status code of {@code 998} is returned in the
 * response of this command.
 */
public interface HelloCommand extends Command {

    int CONTINUE = 200;
    int FIRST_TIME = 201;
    int USERNAME_REQUIRED = 400;
    int ALREADY_SAID_HELLO = 401;

    /**
     * Sets the username of the client in the command
     * @param username the username of the client
     * @return this command instance
     */
    HelloCommand username(String username);

}
