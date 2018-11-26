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
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.db.BaseEntity;

/**
 * Database representation of a user
 */
@DatabaseTable(tableName = "chat_user")
public class ChatUserEntity extends BaseEntity {

    /**
     * The username of the user
     */
    @DatabaseField(columnName = "username", unique = true, canBeNull = false)
    private String username;

    /**
     * The password of the user.
     *
     * <b>NOTE: This is a homework project, no security considerations were made at all regarding safe password storage!
     * Use with caution!</b>
     */
    @DatabaseField (columnName = "password", canBeNull = false)
    private String password;

    /**
     * True, if the user is banned, false otherwise
     */
    @DatabaseField(columnName = "is_banned", canBeNull = false)
    private boolean isBanned;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Converts this chat user to a value object
     * @return the created value object
     */
    public ChatUserVO toVo() {

        ChatUserVO vo = new ChatUserVO();

        vo.setUsername(getUsername());
        vo.setId(getId());
        vo.setBanned(isBanned());

        return vo;

    }
}
