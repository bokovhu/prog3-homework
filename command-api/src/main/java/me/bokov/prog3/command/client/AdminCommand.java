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
 * Represents a single {@code ADMIN} command.
 *
 * The {@code ADMIN} command may be used to handle remote administration tasks, such as banning / unbanning users.
 */
public interface AdminCommand extends Command {

    int SUCCESS = 200;
    int NOT_AUTHENTICATED = 400;
    int INVALID_PASSWORD = 401;

    /**
     * Sets this admin command to handle authentication
     * @param password the admin password
     * @return this command instance
     */
    AdminCommand authenticate (String password);

    /**
     * Sets this admin command to be a banning operation
     * @param userId the ID of the user to ban
     * @return this command instance
     */
    AdminCommand banUser (Long userId);

    /**
     * Sets this admin command to be an unbanning operation
     * @param userId the ID of the user to unban
     * @return this command instance
     */
    AdminCommand unbanUser (Long userId);

    /**
     * Sets this admin command to be a query of all known users in the server
     * @return this command instance
     */
    AdminCommand getAllUsers ();

}
