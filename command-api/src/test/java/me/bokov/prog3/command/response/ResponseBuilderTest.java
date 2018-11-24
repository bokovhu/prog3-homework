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

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import static org.junit.Assert.*;

public class ResponseBuilderTest {

    @Test
    public void build_whenMessageIdAndCodeAreProvided_shouldReturnACorrectResponse () {

        Response response = ResponseBuilder.create()
                .messageId("message-1")
                .code(123)
                .build();

        assertEquals("message-1", response.getMessageId());
        assertEquals(123, response.getCode());
        assertNull(response.getData());

    }

    @Test (expected = IllegalStateException.class)
    public void build_whenMessageIdIsNotProvided_shouldThrowException () {

        Response response = ResponseBuilder.create()
                .code(123)
                .data(Json.createValue("A JSON String"))
                .build();

        assertFalse(true);

    }

    @Test (expected = IllegalStateException.class)
    public void build_whenCodeIsNotProvided_shouldThrowException () {

        Response response = ResponseBuilder.create()
                .messageId("message-1")
                .data(Json.createValue("A JSON String"))
                .build();

        assertFalse(true);

    }

    @Test
    public void build_whenDataIsProvided_shouldHaveData () {

        Response response = ResponseBuilder.create()
                .messageId("message-1")
                .code(123)
                .data(Json.createObjectBuilder().add("a", 1).add("b", 2).build())
                .build();

        assertEquals("message-1", response.getMessageId());
        assertEquals(123, response.getCode());

        JsonValue jv = response.getData();

        assertNotNull(jv);

        JsonObject jo = jv.asJsonObject();

        assertEquals(1, jo.getInt("a"));
        assertEquals(2, jo.getInt("b"));

    }

    @Test (expected = IllegalArgumentException.class)
    public void responseFromMessage_whenNullIsProvided_shouldThrowException () {

        ResponseBuilder.responseFromMessage(null);

    }

    @Test
    public void responseFromMessage_whenOnlyWhitespaceOrEmptyStringIsProvided_shouldThrowException () {

        try {

            ResponseBuilder.responseFromMessage("");

            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Throwable th) {
            assertFalse(true);
        }

        try {

            ResponseBuilder.responseFromMessage("      ");

            assertFalse(true);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Throwable th) {
            assertFalse(true);
        }

    }

    @Test
    public void responseFromMessage_whenNoDataIsPresent_shouldSucceedAndReturnResponseWithoutData () {

        Response response = ResponseBuilder.responseFromMessage("A message-1 100");

        assertEquals("message-1", response.getMessageId());
        assertEquals(100, response.getCode());
        assertNull(response.getData());

    }

    @Test
    public void responseFromMessage_whenDataIsPresent_shouldReturnResponseWithData () {

        Response response = ResponseBuilder.responseFromMessage("A message-1 100 {\"a\":\"b\"}");

        assertEquals("message-1", response.getMessageId());
        assertEquals(100, response.getCode());

        JsonValue jv = response.getData();

        assertNotNull(jv);

        JsonObject jo = jv.asJsonObject();

        assertEquals("b", jo.getString("a"));

    }

    @Test
    public void responseReturnedByBuild_shouldHaveCorrectStringReturnedByToString () {

        Response response = ResponseBuilder.create()
                .messageId("message-1")
                .code(123)
                .data(Json.createValue(12))
                .build();

        assertEquals("A message-1 123 12", response.toString());

        response = ResponseBuilder.create()
                .messageId("message-2")
                .code(456)
                .build();

        assertEquals("A message-2 456", response.toString());

    }

}