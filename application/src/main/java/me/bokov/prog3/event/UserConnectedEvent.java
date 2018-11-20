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

import me.bokov.prog3.command.endpoint.ConnectionInformation;

import java.util.Date;

public class UserConnectedEvent extends BaseEvent {

    private final String username;
    private final ConnectionInformation connectionInformation;
    private final Long userId;

    public UserConnectedEvent(Date eventTimestamp, String eventType, String username, ConnectionInformation connectionInformation, Long userId) {
        super(eventTimestamp, eventType);
        this.username = username;
        this.connectionInformation = connectionInformation;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public ConnectionInformation getConnectionInformation() {
        return connectionInformation;
    }

    public Long getUserId() {
        return userId;
    }
}
