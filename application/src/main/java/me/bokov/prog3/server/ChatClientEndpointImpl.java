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

import me.bokov.prog3.command.endpoint.ChatClientEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.command.server.*;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.EndpointBase;
import me.bokov.prog3.server.command.JoinRoomCommandImpl;
import me.bokov.prog3.server.command.NewMessageCommandImpl;

public class ChatClientEndpointImpl extends EndpointBase implements ChatClientEndpoint {

    public ChatClientEndpointImpl(ClientBase client, ConnectionInformation connectionInformation) {
        super(client, connectionInformation);
    }

    @Override
    public YouAreBannedCommand youAreBanned() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public RoomDeletedCommand roomDeleted() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public NewMessageCommand newMessage() {

        return new NewMessageCommandImpl(client);

    }

    @Override
    public NewInvitationCommand newInvitation() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public JoinRoomCommand joinRoom() {
        return new JoinRoomCommandImpl(client);
    }

}
