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

package me.bokov.prog3.service.common;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;

/**
 * Value object for chat users
 */
public class ChatUserVO implements Serializable {

    /**
     * The database ID of the chat user
     */
    private Long id;

    /**
     * The username of the chat user
     */
    private String username;

    /**
     * Defines whether this user is currently banned
     */
    private Boolean isBanned;

    /**
     * Defines whether this user is currently online
     */
    private Boolean isOnline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    /**
     * Converts this value object to a {@link JsonObject}
     * @return the created {@link JsonObject}
     */
    public JsonObject toJson () {

        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("id", getId())
                .add("username", getUsername())
                .add("isBanned", getBanned());

        if (getOnline() != null) job.add("isOnline", getOnline());

        return job.build();

    }

}
