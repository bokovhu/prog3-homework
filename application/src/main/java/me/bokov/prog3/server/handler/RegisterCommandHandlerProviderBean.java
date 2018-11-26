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

import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.RegisterCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.dao.ChatRoomMembershipDao;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Provides a command handler for the {@code REGISTER} command
 */
@ApplicationScoped
public class RegisterCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("REGISTER");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            if (!context.getChatClient().isSessionValueSet("username")) {
                return ResponseBuilder.create()
                        .messageId(request.getMessageId())
                        .code(999)
                        .build();
            }

            boolean userExists = database.getChatUserDao().queryBuilder()
                    .where().eq("username", context.getChatClient().getSessionValue("username"))
                    .countOf() > 0L;

            if (userExists) {
                return ResponseBuilder.create()
                        .messageId(request.getMessageId())
                        .code(RegisterCommand.ALREADY_REGISTERED)
                        .build();
            }

            ChatUserEntity user = new ChatUserEntity();

            user.setUsername(context.getChatClient().getSessionValue("username").toString());
            user.setBanned(false);
            // TODO: 999 handling of data
            user.setPassword(request.getData().asJsonObject().getString("password"));

            database.getChatUserDao().create(user);

            context.getChatClient().setSessionValue("authenticated", true);
            context.getChatClient().setSessionValue("userId", user.getId());


            // Add user to lobby
            ChatRoomMembershipEntity membership = new ChatRoomMembershipEntity();
            membership.setChatUser(user);
            membership.setChatRoom(database.getChatRoomDao().getLobby());
            database.getChatRoomMembershipDao().create(membership);


            // Send JOIN-ROOM messages
            List <ChatRoomMembershipEntity> memberships = database.getChatRoomMembershipDao()
                    .queryBuilder().where().eq("chat_user_id", user.getId()).query();

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
                    .code(RegisterCommand.SUCCESS)
                    .data(Json.createObjectBuilder().add("userId", user.getId()).build())
                    .build();

        };
    }
}
