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

import javax.json.JsonValue;

/**
 * Represents a single request that is sent through the network.
 *
 * In its string representation, requests take the form <code>A &lt;COMMAND&gt; &lt;MESSAGE-ID&gt; [DATA]</code>,
 * with the <code>DATA</code> part being optional.
 */
public interface Request {

    /**
     * Get the message ID of this request
     * @return the message ID of this request
     */
    String getMessageId();

    /**
     * Get the command of this request
     * @return the command of this request
     */
    String getCommand();

    /**
     * Get the JSON data of this request.
     *
     * If the request has no JSON data, this method will return {@code null}
     * @return the JSON data of this request, or {@code null}
     */
    JsonValue getData();

}
