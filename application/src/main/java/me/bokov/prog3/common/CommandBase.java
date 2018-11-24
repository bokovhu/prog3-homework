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

package me.bokov.prog3.common;

import me.bokov.prog3.command.Command;
import me.bokov.prog3.command.CommandException;
import me.bokov.prog3.command.request.RequestBuilder;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.server.ServerChatClientImpl;

import javax.json.JsonValue;
import java.util.UUID;

public abstract class CommandBase implements Command {

    private final ClientBase chatClient;

    public CommandBase(ClientBase chatClient) {
        this.chatClient = chatClient;
    }

    protected abstract String getCommand ();
    protected abstract JsonValue getData ();

    private String send () {

        String command = getCommand();
        JsonValue data = getData();

        RequestBuilder rb = RequestBuilder.create();

        String messageId = UUID.randomUUID().toString();

        rb.messageId(messageId).command(command);

        if (data != null) rb.data(data);

        chatClient.send(rb.build());

        return messageId;

    }

    @Override
    public Response execute() throws CommandException {
        return chatClient.readResponse(send(), 1000L);
    }

    @Override
    public void executeWithoutAnswer() throws CommandException {
        send();
    }
}
