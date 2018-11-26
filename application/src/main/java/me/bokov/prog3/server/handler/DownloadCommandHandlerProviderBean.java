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
import me.bokov.prog3.command.client.DownloadCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatMessageEntity;
import me.bokov.prog3.util.Config;
import org.apache.commons.io.FileUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

@ApplicationScoped
public class DownloadCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private Config config;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("DOWNLOAD");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            JsonObject json = request.getData().asJsonObject();

            ChatMessageEntity message = database.getChatMessageDao()
                    .queryBuilder().where().eq("file_id", json.getString("fileId"))
                    .queryForFirst();

            JsonObjectBuilder respJson = Json.createObjectBuilder();

            File uploadFile = new File(config.getUploadsDirectory(), json.getString("fileId"));

            respJson.add(
                    "content",
                    Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(uploadFile))
            );
            respJson.add(
                    "size",
                    uploadFile.length()
            );
            if (message.isFileMessage()) {
                respJson.add("fileName", message.getFileName());
            }
            if (message.isImageMessage()) {
                respJson.add("imageExtension", message.getImageExtension());
            }

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(DownloadCommand.SUCCESS)
                    .data(respJson.build())
                    .build();

        };
    }
}
