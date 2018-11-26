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
import me.bokov.prog3.command.client.CreateRoomCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Arrays;
import java.util.Collection;

@ApplicationScoped
public class CreateRoomCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("CREATE-ROOM");
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

            if (!json.containsKey("roomName") || json.isNull("roomName") || json.getString("roomName").isEmpty()) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(CreateRoomCommand.ROOM_NAME_REQUIRED)
                        .build();
            }

            ChatRoomEntity newRoom = new ChatRoomEntity();

            ChatUserEntity user = database.getChatUserDao().queryForId((Long) context.getChatClient().getSessionValue("userId"));

            newRoom.setOwnerChatUser(user);
            newRoom.setIsLobby(false);
            newRoom.setName(json.getString("roomName"));

            database.getChatRoomDao().create(newRoom);

            ChatRoomMembershipEntity membership = new ChatRoomMembershipEntity();

            membership.setChatUser(user);
            membership.setChatRoom(newRoom);

            database.getChatRoomMembershipDao().create(membership);

            context.getChatClient().getClientEndpoint().joinRoom()
                    .roomId(newRoom.getId())
                    .executeWithoutAnswer();

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(CreateRoomCommand.SUCCESS)
                    .build();

        };
    }
}
