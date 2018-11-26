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

package me.bokov.prog3.command.endpoint;

/**
 * This interface is used to describe TCP connections
 */
public interface ConnectionInformation {

    /**
     * Get the local port of the connection
     * @return the local port of the connection
     */
    int getLocalPort();

    /**
     * Get the remote port of the connection
     * @return the remote port of the connection
     */
    int getRemotePort();

    /**
     * Get the local address of the connection
     * @return the local address of the connection
     */
    String getLocalAddress();

    /**
     * Get the remote address of the connection
     * @return the remote address of the connection
     */
    String getRemoteAddress();

}
