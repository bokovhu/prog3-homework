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
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.util.List;

/**
 * Value object for chat rooms
 */
public class ChatRoomVO implements Serializable {

    /**
     * The database ID of the chat room
     */
    private Long id;

    /**
     * The name of the chat room
     */
    private String name;

    /**
     * Defines whether this chat room is a (the) lobby
     */
    private Boolean isLobby;

    /**
     * The owner user of this chat room
     */
    private ChatUserVO owner;

    /**
     * The members of the chat room
     */
    private List<ChatUserVO> members;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLobby() {
        return isLobby;
    }

    public void setLobby(Boolean lobby) {
        isLobby = lobby;
    }

    public ChatUserVO getOwner() {
        return owner;
    }

    public void setOwner(ChatUserVO owner) {
        this.owner = owner;
    }

    public List<ChatUserVO> getMembers() {
        return members;
    }

    public void setMembers(List<ChatUserVO> members) {
        this.members = members;
    }

    /**
     * Converts this value object to a {@link JsonObject}
     * @return the created {@link JsonObject}
     */
    public JsonObject toJson() {

        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("id", getId());

        if (getName() != null) {
            job.add("name", getName());
        }
        if (getLobby() != null) job.add("isLobby", getLobby());

        if (getOwner() != null) job.add("owner", getOwner().toJson());
        if (getMembers() != null) {
            JsonArrayBuilder jab = Json.createArrayBuilder();
            for (ChatUserVO u : getMembers()) {
                jab.add(u.toJson());
            }
            job.add("members", jab.build());
        }

        return job.build();

    }

}
