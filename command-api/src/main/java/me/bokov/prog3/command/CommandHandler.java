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

import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.command.response.Response;

/**
 * CommandHandlers may be used to handle incoming commands
 * @param <CTX> The type of the command handling context
 */
@FunctionalInterface
public interface CommandHandler<CTX> {

    /**
     * Handles a single request of a given command
     * @param context the command handling context
     * @param request the request
     * @return the response of the command to send back to the remote client
     */
    Response handleCommand(CTX context, Request request) throws Exception;

}
