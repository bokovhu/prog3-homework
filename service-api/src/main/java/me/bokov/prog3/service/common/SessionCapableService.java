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

package me.bokov.prog3.service.common;

/**
 * Base interface for all services that are capable of handling session data.
 *
 * Sessions can be though of simple maps, with {@link String} keys and {@link Object} values
 */
public interface SessionCapableService {

    /**
     * Sets a given session value
     * @param key the key in the session
     * @param value the value to set
     */
    void setSessionValue(String key, Object value);

    /**
     * Retrieves a given session value
     * @param key the key in the session
     * @return the value in the session, identified by the key
     */
    Object getSessionValue(String key);

    /**
     * Checks whether a given session value is set
     * @param key the key in the session
     * @return true, if a session entry is found using the key, false otherwise
     */
    boolean isSessionValueSet(String key);

    /**
     * Removes a given session value
     * @param key the key in the session
     */
    void removeSessionValue(String key);

    /**
     * Completely clears the session, removing all entries
     */
    void clearSession();

}
