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
import me.bokov.prog3.command.client.InviteUserCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatInvitationVO;
import me.bokov.prog3.service.common.ChatRoomVO;
import me.bokov.prog3.service.db.entity.ChatInvitationEntity;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@ApplicationScoped
public class InviteUserCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("INVITE-USER");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            JsonObject json = request.getData().asJsonObject();

            ChatUserEntity invitedUser = database.getChatUserDao().queryBuilder()
                    .where().eq("username", json.getString("invitedUsername"))
                    .queryForFirst();

            ChatUserEntity invitorUser = database.getChatUserDao().queryForId(
                    (Long) context.getChatClient().getSessionValue("userId")
            );

            ChatRoomEntity room = database.getChatRoomDao().queryForId(json.getJsonNumber("roomId").longValue());


            ChatInvitationEntity invitation = new ChatInvitationEntity();
            invitation.setRoom(room);
            invitation.setInvitationId(UUID.randomUUID().toString());
            invitation.setInvitor(invitorUser);
            invitation.setInvitedUser(invitedUser);

            database.getChatInvitationDao().create(invitation);

            chatServer.clientByUserId(invitedUser.getId())
                    .ifPresent(
                            chatClientEndpoint -> {

                                ChatInvitationVO invitationVo = new ChatInvitationVO();

                                ChatRoomVO roomVo = new ChatRoomVO();
                                roomVo.setId(room.getId());
                                roomVo.setName(room.getName());
                                roomVo.setLobby(room.getIsLobby());
                                invitationVo.setRoom(roomVo);

                                invitationVo.setInvitedUser(invitedUser.toVo());
                                invitationVo.setInvitor(invitorUser.toVo());
                                invitationVo.setInvitationId(invitation.getInvitationId());

                                chatClientEndpoint.newInvitation()
                                        .invitation(invitationVo.toJson())
                                        .execute();

                            }
                    );

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(InviteUserCommand.SUCCESS)
                    .build();

        };
    }
}
