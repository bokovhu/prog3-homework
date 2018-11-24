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

package me.bokov.prog3.command.response;

import javax.json.Json;
import javax.json.JsonValue;
import java.io.StringReader;
import java.io.StringWriter;

public final class ResponseBuilder {

    private String messageId = null;
    private Integer code = null;
    private JsonValue data = null;

    private ResponseBuilder() {

    }

    public static ResponseBuilder create() {
        return new ResponseBuilder();
    }

    public static Response responseFromMessage(String rawMessage) {

        if (rawMessage == null) {
            throw new IllegalArgumentException("Raw message cannot be null!");
        }

        String trimmed = rawMessage.trim();

        if (!trimmed.startsWith("A ")) {
            throw new IllegalArgumentException("Not a response!");
        }

        String[] splitted = trimmed.substring(2).split(" ", 3);

        if (splitted.length < 2) {
            throw new IllegalArgumentException("Malformed raw message!");
        }

        String messageId = splitted[0];
        Integer code = Integer.parseInt(splitted[1]);
        JsonValue data = null;

        if (splitted.length == 3) {

            StringReader sr = new StringReader(splitted[2]);
            data = Json.createReader(sr).readValue();

        }

        return create().messageId(messageId).code(code).data(data).build();

    }

    public Response build() {

        if (this.messageId == null) {
            throw new IllegalStateException("Message ID cannot be null!");
        }

        if (this.code == null) {
            throw new IllegalStateException("Code cannot be null!");
        }

        return new ResponseImpl(this.messageId, this.code, this.data);

    }

    public ResponseBuilder messageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public ResponseBuilder code(int code) {
        this.code = code;
        return this;
    }

    public ResponseBuilder data(JsonValue data) {
        this.data = data;
        return this;
    }

    class ResponseImpl implements Response {

        private final String messageId;
        private final Integer code;
        private final JsonValue data;

        ResponseImpl(String messageId, Integer code, JsonValue data) {
            this.messageId = messageId;
            this.code = code;
            this.data = data;
        }

        @Override
        public String getMessageId() {
            return messageId;
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public JsonValue getData() {
            return data;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            sb.append("A ").append(messageId).append(" ").append(code);

            if (data != null) {

                StringWriter sw = new StringWriter();
                Json.createWriter(sw).write(data);
                sb.append(" ").append(sw.toString());

            }

            return sb.toString();

        }

    }

}
