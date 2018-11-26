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

package me.bokov.prog3.client;

import me.bokov.prog3.client.command.*;
import me.bokov.prog3.command.client.*;
import me.bokov.prog3.command.endpoint.ChatServerEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.EndpointBase;

/**
 * The default chat server endpoint implementation
 */
public class ChatServerEndpointImpl extends EndpointBase implements ChatServerEndpoint {

    public ChatServerEndpointImpl(ClientBase client, ConnectionInformation connectionInformation) {
        super(client, connectionInformation);
    }

    @Override
    public HelloCommand hello() {
        return new HelloCommandImpl(client);
    }

    @Override
    public LoginCommand login() {
        return new LoginCommandImpl(client);
    }

    @Override
    public RegisterCommand register() {
        return new RegisterCommandImpl(client);
    }

    @Override
    public CreateRoomCommand createRoom() {
        return new CreateRoomCommandImpl(client);
    }

    @Override
    public DisconnectCommand disconnect() {
        return new DisconnectCommandImpl(client);
    }

    @Override
    public GetRoomCommand getRoom() {
        return new GetRoomCommandImpl(client);
    }

    @Override
    public AcceptInvitationCommand acceptInvitation() {
        return new AcceptInvitationCommandImpl(client);
    }

    @Override
    public InviteUserCommand inviteUser() {
        return new InviteUserCommandImpl(client);
    }

    @Override
    public DownloadCommand download() {
        return new DownloadCommandImpl(client);
    }

    @Override
    public SendMessageCommand sendMessage() {
        return new SendMessageCommandImpl(client);
    }

    @Override
    public SendImageCommand sendImage() {
        return new SendImageCommandImpl(client);
    }

    @Override
    public SendFileCommand sendFile() {
        return new SendFileCommandImpl(client);
    }

    @Override
    public LeaveRoomCommand leaveRoom() {
        return new LeaveRoomCommandImpl(client);
    }

    @Override
    public DeleteRoomCommand deleteRoom() {
        return new DeleteRoomCommandImpl(client);
    }

    @Override
    public GetMessagesCommand getMessages() {
        return new GetMessagesCommandImpl(client);
    }

    @Override
    public AdminCommand admin() {
        return new AdminCommandImpl(client);
    }
}
