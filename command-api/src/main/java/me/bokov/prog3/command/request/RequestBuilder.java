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

package me.bokov.prog3.command.request;

import javax.json.Json;
import javax.json.JsonValue;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

public final class RequestBuilder {

    private String messageId = UUID.randomUUID().toString();
    private String command = null;
    private JsonValue data = null;

    private RequestBuilder () {

    }

    public static RequestBuilder create () {
        return new RequestBuilder();
    }

    public static Request requestFromMessage (String rawMessage) {

        if (rawMessage == null) {
            throw new IllegalArgumentException("Raw message cannot be null!");
        }

        String [] splitted = rawMessage.split(" ", 3);

        if (splitted.length < 2) {
            throw new IllegalArgumentException("Malformed raw message!");
        }

        String messageId = splitted [0];
        String command = splitted [1];
        JsonValue data = null;

        if (splitted.length == 3) {

            StringReader sr = new StringReader(splitted [2]);
            data = Json.createReader(sr).readValue();

        }

        return create().messageId(messageId).command(command).data(data).build();

    }

    public Request build () {

        if (command == null) {
            throw new IllegalStateException("Command cannot be null!");
        }

        return new RequestImpl(this.messageId, this.command, this.data);

    }

    public RequestBuilder messageId (String messageId) {
        this.messageId = messageId;
        return this;
    }

    public RequestBuilder command (String command) {
        this.command = command;
        return this;
    }

    public RequestBuilder data (JsonValue data) {
        this.data = data;
        return this;
    }

    class RequestImpl implements Request {

        private final String messageId;
        private final String command;
        private final JsonValue data;

        RequestImpl(String messageId, String command, JsonValue data) {
            this.messageId = messageId;
            this.command = command;
            this.data = data;
        }

        @Override
        public String getMessageId() {
            return messageId;
        }

        @Override
        public String getCommand() {
            return command;
        }

        @Override
        public JsonValue getData() {
            return data;
        }

        @Override
        public String toString () {

            StringBuilder sb = new StringBuilder();

            sb.append(messageId).append(" ").append(command);

            if (data != null) {

                StringWriter sw = new StringWriter();
                Json.createWriter(sw).write(data);

                sb.append(" ").append(sw.toString());

            }

            return sb.toString();

        }

    }

}
