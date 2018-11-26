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
import me.bokov.prog3.command.client.SendMessageCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatMessageVO;
import me.bokov.prog3.service.common.ChatRoomVO;
import me.bokov.prog3.service.db.entity.ChatMessageEntity;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@ApplicationScoped
public class SendMessageCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("SEND-MESSAGE");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            JsonObject json = request.getData().asJsonObject();

            ChatRoomEntity room = database.getChatRoomDao()
                    .queryForId(json.getJsonNumber("roomId").longValue());

            ChatUserEntity user = database.getChatUserDao()
                    .queryForId((Long) context.getChatClient().getSessionValue("userId"));

            ChatMessageEntity message = new ChatMessageEntity();

            message.setTextMessage(true);
            message.setFileMessage(false);
            message.setImageMessage(false);
            message.setSentDate(new Date());
            message.setSentBy(user);
            message.setMessageText(json.getString("messageText"));
            message.setRoom(room);

            database.getChatMessageDao().create(message);


            ChatMessageVO vo = new ChatMessageVO();
            vo.setId(message.getId());
            vo.setSentBy(user.toVo());
            vo.setRoom(new ChatRoomVO());
            vo.getRoom().setId(room.getId());
            vo.setTextMessage(message.isTextMessage());
            vo.setFileMessage(message.isFileMessage());
            vo.setImageMessage(message.isImageMessage());
            vo.setMessageText(message.getMessageText());
            vo.setSentDate(message.getSentDate());

            chatServer.clientsInRoom(room.getId())
                    .forEach(
                            roomClient -> roomClient.newMessage()
                                    .roomId(room.getId().toString())
                                    .message(vo.toJson())
                                    .executeWithoutAnswer()
                    );

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(SendMessageCommand.SUCCESS)
                    .build();

        };
    }
}
