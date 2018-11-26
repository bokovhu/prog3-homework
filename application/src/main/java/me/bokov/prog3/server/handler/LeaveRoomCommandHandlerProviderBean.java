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
import me.bokov.prog3.command.client.LeaveRoomCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides a command handler for the {@code LEAVE-ROOM} command
 */
@ApplicationScoped
public class LeaveRoomCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("LEAVE-ROOM");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            if (context.getChatClient().isBanned()) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.BANNED)
                        .build();
            }

            JsonObject json = request.getData().asJsonObject();

            ChatRoomMembershipEntity membership = database.getChatRoomMembershipDao()
                    .queryBuilder().where().eq("chat_room_id", json.getJsonNumber("roomId").longValue())
                    .and().eq("chat_user_id", context.getChatClient().getSessionValue("userId"))
                    .queryForFirst();

            database.getChatRoomMembershipDao().delete(membership);

            chatServer.clientsInRoom(json.getJsonNumber("roomId").longValue())
                    .forEach(
                            c -> c.roomChanged().roomId(json.getJsonNumber("roomId").longValue()).executeWithoutAnswer()
                    );

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(LeaveRoomCommand.SUCCESS)
                    .build();

        };
    }
}
