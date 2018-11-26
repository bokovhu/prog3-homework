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
import java.io.Serializable;

public class ChatInvitationVO implements Serializable {

    private ChatUserVO invitor;
    private ChatUserVO invitedUser;
    private ChatRoomVO room;
    private String invitationId;

    public ChatUserVO getInvitor() {
        return invitor;
    }

    public void setInvitor(ChatUserVO invitor) {
        this.invitor = invitor;
    }

    public ChatUserVO getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(ChatUserVO invitedUser) {
        this.invitedUser = invitedUser;
    }

    public ChatRoomVO getRoom() {
        return room;
    }

    public void setRoom(ChatRoomVO room) {
        this.room = room;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public JsonObject toJson () {

        return Json.createObjectBuilder()
                .add("invitor", getInvitor().toJson())
                .add("invitedUser", getInvitedUser().toJson())
                .add("room", getRoom().toJson())
                .add("invitationId", getInvitationId())
                .build();

    }

}
