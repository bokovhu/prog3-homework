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

import me.bokov.prog3.command.response.Response;

/**
 * The base interface for all commands
 */
public interface Command {

    /**
     * The user was banned
     */
    int BANNED = 998;

    /**
     * The request was invalid
     */
    int INVALID = 999;

    /**
     * Executes this command and waits for the response, blocking the caller's thread if necessary.
     * @return the response of the command execution
     * @throws CommandException if the status code in the response in an error
     */
    Response execute() throws CommandException;

    /**
     * Executes this command and discards its response. This method returns immediately after the method call
     * @throws CommandException
     */
    void executeWithoutAnswer() throws CommandException;

}
