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

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.UUID;

import static org.junit.Assert.*;

public class RequestBuilderTest {

    @Test
    public void build_whenNoMessageIdIsProvided_shouldHaveMessageIdAsARandomUUID () {

        Request request = RequestBuilder.create()
                .command("COMMAND")
                .build();

        assertEquals("COMMAND", request.getCommand());

        UUID uuid = UUID.fromString(request.getMessageId());

        assertEquals(UUID.randomUUID().version(), uuid.version());

        assertNull(request.getData());

    }

    @Test
    public void build_whenMessageIdIsProvided_shouldOverrideDefaultMessageId () {

        Request request = RequestBuilder.create()
                .messageId("message-1")
                .command("COMMAND")
                .build();

        assertEquals("COMMAND", request.getCommand());
        assertEquals("message-1", request.getMessageId());
        assertNull(request.getData());

    }

    @Test
    public void build_whenDataIsProvided_shouldHaveData () {

        Request request = RequestBuilder.create()
                .command("COMMAND")
                .data(Json.createValue("test"))
                .build();

        assertEquals("COMMAND", request.getCommand());

        JsonValue jv = request.getData();

        assertNotNull(jv);

        assertEquals(JsonValue.ValueType.STRING, jv.getValueType());
        assertEquals("test", ( (JsonString) jv ).getString());

    }

    @Test (expected = IllegalStateException.class)
    public void build_whenNoCommandIsProvided_shouldThrowException () {

        Request request = RequestBuilder.create().build();

        assertFalse(true);

    }

    @Test (expected = IllegalArgumentException.class)
    public void requestFromMessage_whenNullIsProvided_shouldThrowException () {

        Request request = RequestBuilder.requestFromMessage(null);

        assertFalse(true);

    }

    @Test
    public void requestFromMessage_whenOnlyWhitespaceOrEmptyStringIsProvided_shouldThrowException () {

        try {

            Request request = RequestBuilder.requestFromMessage("");

            assertFalse(true);

        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Throwable th) {
            assertFalse(true);
        }

        try {

            Request request = RequestBuilder.requestFromMessage("       ");

            assertFalse(true);

        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Throwable th) {
            assertFalse(true);
        }

    }

    @Test
    public void test_requestFromMessage_whenCorrectStringIsProvided_thenShouldWork () {

        Request request = RequestBuilder.requestFromMessage("Q message-1 HELLO");

        assertEquals("message-1", request.getMessageId());
        assertEquals("HELLO", request.getCommand());
        assertNull(request.getData());

        request = RequestBuilder.requestFromMessage("Q message-2 HI {\"name\":\"Test\"}");

        assertEquals("message-2", request.getMessageId());
        assertEquals("HI", request.getCommand());
        assertNotNull(request.getData());

        JsonValue jv = request.getData();
        JsonObject jo = jv.asJsonObject();

        assertEquals("Test", jo.getString("name"));

    }

    @Test
    public void test_toString_whenNoDataIsProvided_thenMessageContainsNoData () {

        Request request = RequestBuilder.create()
                .messageId("message-1")
                .command("HELLO")
                .build();

        String reqString = request.toString();

        assertEquals("Q message-1 HELLO", reqString.trim());

    }

    @Test
    public void test_toString_whenDataIsProvided_thenMessageContainsData () {

        Request request = RequestBuilder.create()
                .messageId("message-1")
                .command("HELLO")
                .data(Json.createValue(13.4))
                .build();

        String reqString = request.toString();

        assertEquals("Q message-1 HELLO 13.4", reqString.trim());

    }

}