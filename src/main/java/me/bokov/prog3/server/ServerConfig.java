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

package me.bokov.prog3.server;

import java.io.Serializable;

public class ServerConfig implements Serializable {

    private long clientIntroductionTimeout = 3000L;
    private boolean passwordEnabled = false;
    private String password = null;
    private int port;
    private String bindToHost;

    public long getClientIntroductionTimeout() {
        return clientIntroductionTimeout;
    }

    public void setClientIntroductionTimeout(long clientIntroductionTimeout) {
        this.clientIntroductionTimeout = clientIntroductionTimeout;
    }

    public boolean isPasswordEnabled() {
        return passwordEnabled;
    }

    public void setPasswordEnabled(boolean passwordEnabled) {
        this.passwordEnabled = passwordEnabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBindToHost() {
        return bindToHost;
    }

    public void setBindToHost(String bindToHost) {
        this.bindToHost = bindToHost;
    }
}
