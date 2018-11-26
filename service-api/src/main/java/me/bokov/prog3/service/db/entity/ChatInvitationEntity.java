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

package me.bokov.prog3.service.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import me.bokov.prog3.service.db.BaseEntity;

@DatabaseTable (tableName = "chat_invitation")
public class ChatInvitationEntity extends BaseEntity {

    @DatabaseField (columnName = "invitor_id", foreign = true, canBeNull = false)
    private ChatUserEntity invitor;

    @DatabaseField (columnName = "invited_user_id", foreign = true, canBeNull = false)
    private ChatUserEntity invitedUser;

    @DatabaseField (columnName = "room_id", foreign = true, canBeNull = false)
    private ChatRoomEntity room;

    @DatabaseField (columnName = "invitation_id", unique = true, canBeNull = false)
    private String invitationId;

    public ChatUserEntity getInvitor() {
        return invitor;
    }

    public void setInvitor(ChatUserEntity invitor) {
        this.invitor = invitor;
    }

    public ChatUserEntity getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(ChatUserEntity invitedUser) {
        this.invitedUser = invitedUser;
    }

    public ChatRoomEntity getRoom() {
        return room;
    }

    public void setRoom(ChatRoomEntity room) {
        this.room = room;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }
}
