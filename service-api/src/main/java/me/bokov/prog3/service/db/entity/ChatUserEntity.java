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

import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.db.BaseEntity;

import java.util.HashMap;
import java.util.Map;

public class ChatUserEntity extends BaseEntity {

    private String username;
    private String banState;
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

    public ChatUserVO toVo () {

        ChatUserVO vo = new ChatUserVO();

        vo.setUsername(getUsername());
        vo.setId(getId());
        vo.setBanState(getBanState());

        return vo;

    }

}
