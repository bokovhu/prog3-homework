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
import java.util.stream.Collectors;

public class ChatRoomVO implements Serializable {

    private Long id;
    private String name;
    private Boolean isLobby;
    private ChatUserVO owner;
    private List <ChatUserVO> members;

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

    public JsonObject toJson () {

        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("id", getId())
                .add("name", getName())
                .add("isLobby", getLobby());

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
