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
import me.bokov.prog3.command.client.DeleteRoomCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatMessageEntity;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.util.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class DeleteRoomCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private Config config;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("DELETE-ROOM");
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

            if (!json.containsKey("roomId") || json.isNull("roomId")) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(DeleteRoomCommand.INVALID)
                        .build();
            }

            ChatRoomEntity room = database.getChatRoomDao().queryForId(json.getJsonNumber("roomId").longValue());

            if (room == null) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(DeleteRoomCommand.INVALID_ROOM_ID)
                        .build();
            }

            if (room.getIsLobby() || room.getOwnerChatUser() == null || !room.getOwnerChatUser().getId().equals(context.getChatClient().getSessionValue("userId"))) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(DeleteRoomCommand.NOT_OWNER)
                        .build();
            }

            if (database.getChatRoomMembershipDao().queryBuilder().where().eq("chat_room_id", room.getId()).countOf() > 1L) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(DeleteRoomCommand.NOT_EMPTY)
                        .build();
            }

            List <ChatMessageEntity> roomMessages = database.getChatMessageDao().queryForEq("room_id", room.getId());

            for (ChatMessageEntity msg : roomMessages) {

                if (msg.isFileMessage() || msg.isImageMessage()) {
                    File file = new File(config.getUploadsDirectory(), msg.getFileId());
                    if (file.exists()) {
                        file.delete();
                    }
                }

                database.getChatMessageDao().delete(msg);

            }

            database.getChatRoomMembershipDao().delete(database.getChatRoomMembershipDao().queryForEq("chat_room_id", room.getId()));

            database.getChatInvitationDao().delete(database.getChatInvitationDao().queryForEq("room_id", room.getId()));

            database.getChatRoomDao().delete(room);

            return ResponseBuilder.create().messageId(request.getMessageId())
                    .code(DeleteRoomCommand.SUCCESS)
                    .build();

        };
    }
}
