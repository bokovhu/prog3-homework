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

import javax.json.JsonValue;

/**
 * Represents a single response that is sent through the network.
 *
 * In its string representation, responses take the form <code>Q &lt;MESSAGE-ID&gt; &lt;CODE&gt; [DATA]</code>,
 * with the <code>DATA</code> part being optional.
 *
 * The message ID is equal to the message ID of the request that is object is the response of.
 * The code is the status / error code of the response.
 */
public interface Response {

    /**
     * Get the message ID of the response. This message ID is equal to the message ID of the request whose response this
     * object is.
     * @return the message ID
     */
    String getMessageId();

    /**
     * Gets the status / error code of the response
     * @return the status / error code
     */
    int getCode();

    /**
     * Gets the JSON data of the response. As JSON data is optional, this method may return {@code null}
     * @return the JSON data of the response, or {@code null}
     */
    JsonValue getData();

}
