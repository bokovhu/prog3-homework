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

package me.bokov.prog3.server.handler;

import me.bokov.prog3.command.Command;
import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.AcceptInvitationCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatInvitationEntity;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Provides a command handler for the {@code ACCEPT-INVITATION} command
 */
@ApplicationScoped
public class AcceptInvitationCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("ACCEPT-INVITATION");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            if (context.getChatClient().isBanned()) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.BANNED)
                        .build();
            }

            if (request.getData() == null || request.getData().getValueType() != JsonValue.ValueType.OBJECT) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.INVALID)
                        .build();
            }

            JsonObject json = request.getData().asJsonObject();

            if (!json.containsKey("invitationId") || json.isNull("invitationId")) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.INVALID)
                        .build();
            }

            ChatInvitationEntity invitation = database.getChatInvitationDao().getByInvitationId(json.getString("invitationId"));

            if (invitation == null) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(AcceptInvitationCommand.INVALID_INVITATION_ID)
                        .build();
            }

            if (database.getChatRoomMembershipDao().queryBuilder().where().eq("chat_room_id", invitation.getRoom().getId())
                    .and().eq("chat_user_id", invitation.getInvitedUser().getId()).countOf() > 0L) {
                return ResponseBuilder.create()
                        .messageId(request.getMessageId())
                        .code(AcceptInvitationCommand.ALREADY_MEMBER)
                        .build();
            }

            ChatRoomMembershipEntity newMembership = new ChatRoomMembershipEntity();

            newMembership.setChatUser(database.getChatUserDao().queryForId(invitation.getInvitedUser().getId()));
            newMembership.setChatRoom(database.getChatRoomDao().queryForId(invitation.getRoom().getId()));

            database.getChatRoomMembershipDao().create(newMembership);

            database.getChatInvitationDao().delete(invitation);

            context.getChatClient().getClientEndpoint().joinRoom()
                    .roomId(invitation.getRoom().getId())
                    .executeWithoutAnswer();

            // Send JOIN-ROOM messages
            List<ChatRoomMembershipEntity> memberships = database.getChatRoomMembershipDao()
                    .queryBuilder().where().eq("chat_user_id", invitation.getInvitedUser().getId()).query();

            for (ChatRoomMembershipEntity m : memberships) {
                context.getChatClient().getClientEndpoint()
                        .joinRoom().roomId(m.getChatRoom().getId())
                        .executeWithoutAnswer();
                chatServer.clientsInRoom(m.getChatRoom().getId())
                        .forEach(
                                c -> c.roomChanged().roomId(m.getChatRoom().getId()).executeWithoutAnswer()
                        );
            }

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(AcceptInvitationCommand.SUCCESS)
                    .build();

        };
    }
}
