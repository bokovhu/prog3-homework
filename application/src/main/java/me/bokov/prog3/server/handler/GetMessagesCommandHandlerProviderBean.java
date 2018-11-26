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
import me.bokov.prog3.command.client.GetMessagesCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatMessageVO;
import me.bokov.prog3.service.common.ChatRoomVO;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.db.BaseEntity;
import me.bokov.prog3.service.db.entity.ChatMessageEntity;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetMessagesCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("GET-MESSAGES");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            JsonObject json = request.getData().asJsonObject();

            ChatRoomEntity room = database.getChatRoomDao().queryForId(
                    json.getJsonNumber("roomId").longValue()
            );

            List<Long> members = database.getChatRoomMembershipDao().queryBuilder()
                    .where().eq("chat_room_id", room.getId())
                    .query().stream().map(ChatRoomMembershipEntity::getChatUser)
                    .map(BaseEntity::getId).collect(Collectors.toList());

            ChatRoomVO roomVo = new ChatRoomVO();
            roomVo.setId(room.getId());
            Map<Long, ChatUserVO> userMap = new HashMap<>();

            for (ChatUserEntity user : database.getChatUserDao().queryBuilder()
                    .where().in("id", members).query()) {
                userMap.put(user.getId(), user.toVo());
            }

            JsonArrayBuilder jab = Json.createArrayBuilder();

            for (ChatMessageEntity message : database.getChatMessageDao().queryForEq("room_id", room.getId())) {

                ChatMessageVO vo = new ChatMessageVO();
                vo.setId(message.getId());
                vo.setSentBy(userMap.get(message.getSentBy().getId()));
                vo.setRoom(roomVo);
                vo.setTextMessage(message.isTextMessage());
                vo.setFileMessage(message.isFileMessage());
                vo.setImageMessage(message.isImageMessage());
                vo.setMessageText(message.getMessageText());
                vo.setFileId(message.getFileId());
                vo.setFileName(message.getFileName());
                vo.setFileSize(message.getFileSize());
                vo.setImageExtension(message.getImageExtension());
                vo.setSentDate(message.getSentDate());

                jab.add(vo.toJson());

            }

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(GetMessagesCommand.SUCCESS)
                    .data(Json.createObjectBuilder().add("messages", jab.build()).build())
                    .build();

        };
    }
}
