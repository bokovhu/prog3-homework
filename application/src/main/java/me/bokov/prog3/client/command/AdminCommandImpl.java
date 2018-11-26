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

import me.bokov.prog3.command.client.AdminCommand;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.CommandBase;

import javax.json.Json;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class AdminCommandImpl extends CommandBase implements AdminCommand {

    private String method = null;
    private Map <String, Object> params = new HashMap<>();

    public AdminCommandImpl(ClientBase chatClient) {
        super(chatClient);
    }

    @Override
    public AdminCommand authenticate(String password) {
        this.method = "AUTHENTICATE";
        this.params.put("password", password);
        return this;
    }

    @Override
    public AdminCommand banUser(Long userId) {
        this.method = "BAN_USER";
        this.params.put("userId", userId);
        return this;
    }

    @Override
    public AdminCommand unbanUser(Long userId) {
        this.method = "UNBAN_USER";
        this.params.put("userId", userId);
        return this;
    }

    @Override
    public AdminCommand getAllUsers() {
        this.method = "GET_ALL_USERS";
        return this;
    }

    @Override
    protected String getCommand() {
        return "ADMIN";
    }

    @Override
    protected JsonValue getData() {
        return Json.createObjectBuilder()
                .add("method", method)
                .add("params", Json.createObjectBuilder(params).build())
                .build();
    }
}
