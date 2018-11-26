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


import me.bokov.prog3.command.server.*;

/**
 * The callable endpoint service of a chat client, connected to a chat server. Using commands found in this interface,
 * the server can initiate messaging to any of its clients.
 */
public interface ChatClientEndpoint extends Endpoint {

    /**
     * Create a new {@code YOU-ARE-BANNED} command
     * @return the created command
     */
    YouAreBannedCommand youAreBanned();

    /**
     * Create a new {@code NEW-MESSAGE} command
     * @return the created command
     */
    NewMessageCommand newMessage();

    /**
     * Create a new {@code NEW-INVITATION} command
     * @return the created command
     */
    NewInvitationCommand newInvitation();

    /**
     * Create a new {@code JOIN-ROOM} command
     * @return the created command
     */
    JoinRoomCommand joinRoom ();

    /**
     * Create a new {@code ROOM-CHANGED} command
     * @return the created command
     */
    RoomChangedCommand roomChanged ();

}
