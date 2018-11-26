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

package me.bokov.prog3.client.command;

import me.bokov.prog3.command.client.DeleteRoomCommand;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.CommandBase;

import javax.json.Json;
import javax.json.JsonValue;

public class DeleteRoomCommandImpl extends CommandBase implements DeleteRoomCommand {

    private Long roomId;

    public DeleteRoomCommandImpl(ClientBase chatClient) {
        super(chatClient);
    }

    @Override
    public DeleteRoomCommand roomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    @Override
    protected String getCommand() {
        return "DELETE-ROOM";
    }

    @Override
    protected JsonValue getData() {
        return Json.createObjectBuilder().add("roomId", roomId).build();
    }
}
