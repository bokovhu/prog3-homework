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

/**
 * Database representation of a chat room
 */
@DatabaseTable(tableName = "chat_room")
public class ChatRoomEntity extends BaseEntity {

    /**
     * The name of the chat room
     */
    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    /**
     * True, if this room is the lobby room, false otherwise (false in case of all user created rooms)
     */
    @DatabaseField (columnName = "is_lobby", canBeNull = false, columnDefinition = "BOOLEAN")
    private Boolean isLobby;

    /**
     * The owner of the chat room
     */
    @DatabaseField(columnName = "owner_chat_user_id", foreign = true, canBeNull = true)
    private ChatUserEntity ownerChatUser;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatUserEntity getOwnerChatUser() {
        return ownerChatUser;
    }

    public void setOwnerChatUser(ChatUserEntity ownerChatUser) {
        this.ownerChatUser = ownerChatUser;
    }

    public Boolean getIsLobby() {
        return isLobby;
    }

    public void setIsLobby(Boolean lobby) {
        isLobby = lobby;
    }
}
