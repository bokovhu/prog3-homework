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

@DatabaseTable(tableName = "chat_user")
public class ChatUserEntity extends BaseEntity {

    @DatabaseField(columnName = "username", unique = true, canBeNull = false)
    private String username;

    // VERY, VERY, VERY BAD PRACTICE!!!!!!!! DON'T EVER DO THIS!!!!
    @DatabaseField (columnName = "password", canBeNull = false)
    private String password;

    @DatabaseField(columnName = "ban_state", canBeNull = false, defaultValue = "NOT_BANNED")
    private String banState;

    @DatabaseField(columnName = "banned_ip")
    private String bannedIp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBanState() {
        return banState;
    }

    public void setBanState(String banState) {
        this.banState = banState;
    }

    public String getBannedIp() {
        return bannedIp;
    }

    public void setBannedIp(String bannedIp) {
        this.bannedIp = bannedIp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ChatUserVO toVo() {

        ChatUserVO vo = new ChatUserVO();

        vo.setUsername(getUsername());
        vo.setId(getId());
        vo.setBanState(getBanState());

        return vo;

    }
}
