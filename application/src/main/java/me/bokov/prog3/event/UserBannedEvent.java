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

package me.bokov.prog3.event;

import java.util.Date;

public class UserBannedEvent extends BaseEvent {

    private final String username;
    private final boolean bannedByIp;
    private final boolean bannedByUsername;

    public UserBannedEvent(Date eventTimestamp, String eventType, String username, boolean bannedByIp, boolean bannedByUsername) {
        super(eventTimestamp, eventType);
        this.username = username;
        this.bannedByIp = bannedByIp;
        this.bannedByUsername = bannedByUsername;
    }

    public String getUsername() {
        return username;
    }

    public boolean isBannedByIp() {
        return bannedByIp;
    }

    public boolean isBannedByUsername() {
        return bannedByUsername;
    }
}
