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

import me.bokov.prog3.command.client.*;

/**
 * The callable endpoint service of a chat server. The chat client can initiate messaging through this interface's
 * command methods
 */
public interface ChatServerEndpoint extends Endpoint {

    /**
     * Create a new {@code HELLO} command
     * @return the created command
     */
    HelloCommand hello();

    /**
     * Create a new {@code LOGIN} command
     * @return the created command
     */
    LoginCommand login();

    /**
     * Create a new {@code REGISTER} command
     * @return the created command
     */
    RegisterCommand register ();

    /**
     * Create a new {@code CREATE-ROOM} command
     * @return the created command
     */
    CreateRoomCommand createRoom();

    /**
     * Create a new {@code DISCONNECT} command
     * @return the created command
     */
    DisconnectCommand disconnect();

    /**
     * Create a new {@code GET-ROOM} command
     * @return the created command
     */
    GetRoomCommand getRoom ();

    /**
     * Create a new {@code ACCEPT-INVITATION} command
     * @return the created command
     */
    AcceptInvitationCommand acceptInvitation ();

    /**
     * Create a new {@code INVITE-USER} command
     * @return the created command
     */
    InviteUserCommand inviteUser ();

    /**
     * Create a new {@code DOWNLOAD} command
     * @return the created command
     */
    DownloadCommand download ();

    /**
     * Create a new {@code SEND-MESSAGE} command
     * @return the created command
     */
    SendMessageCommand sendMessage ();

    /**
     * Create a new {@code SEND-IMAGE} command
     * @return the created command
     */
    SendImageCommand sendImage ();

    /**
     * Create a new {@code SEND-FILE} command
     * @return the created command
     */
    SendFileCommand sendFile ();

    /**
     * Create a new {@code LEAVE-ROOM} command
     * @return the created command
     */
    LeaveRoomCommand leaveRoom ();

    /**
     * Create a new {@code DELETE-ROOM} command
     * @return the created command
     */
    DeleteRoomCommand deleteRoom ();

    /**
     * Create a new {@code GET-MESSAGES} command
     * @return the created command
     */
    GetMessagesCommand getMessages ();

    /**
     * Create a new {@code ADMIN} command
     * @return the created command
     */
    AdminCommand admin ();

}
