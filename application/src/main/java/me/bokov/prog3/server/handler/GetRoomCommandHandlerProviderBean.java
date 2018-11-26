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
import me.bokov.prog3.command.client.GetRoomCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatRoomVO;
import me.bokov.prog3.service.db.BaseEntity;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetRoomCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("GET-ROOM");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            JsonObject requestJson = request.getData().asJsonObject();

            ChatRoomEntity room = database.getChatRoomDao().queryForId(requestJson.getJsonNumber("roomId").longValue());
            ChatUserEntity roomOwner = null;

            if (room.getOwnerChatUser() != null && room.getOwnerChatUser().getId() != null) {
                roomOwner = database.getChatUserDao().queryForId(room.getOwnerChatUser().getId());
            }

            List <ChatRoomMembershipEntity> memberships = database.getChatRoomMembershipDao().queryForEq("chat_room_id", room.getId());
            List <ChatUserEntity> members = database.getChatUserDao().queryBuilder()
                    .where().in("id", memberships.stream().map(ChatRoomMembershipEntity::getChatUser).map(BaseEntity::getId).collect(Collectors.toList()))
                    .query();

            ChatRoomVO vo = new ChatRoomVO();
            vo.setId(room.getId());
            vo.setName(room.getName());
            vo.setLobby(room.getIsLobby());

            if (roomOwner != null) {
                vo.setOwner(roomOwner.toVo());
            }

            vo.setMembers(
                    members.stream().map(ChatUserEntity::toVo)
                            .peek(
                                    u -> u.setOnline(chatServer.clientByUserId(u.getId()).isPresent())
                            )
                            .collect(Collectors.toList())
            );

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(GetRoomCommand.SUCCESS)
                    .data(Json.createObjectBuilder().add("room", vo.toJson()).build())
                    .build();

        };
    }
}
