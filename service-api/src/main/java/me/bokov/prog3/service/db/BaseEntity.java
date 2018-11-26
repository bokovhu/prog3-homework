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

package me.bokov.prog3.service.db;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Base class for all database entities
 */
public abstract class BaseEntity implements Serializable {

    /**
     * The ID (primary key) of this entity.
     *
     * This is an auto generated number, where values are supplied by the underlying database implementation
     */
    @DatabaseField(columnName = "id", generatedId = true)
    private Long id;

    /**
     * Retrieves the ID of this entity
     * @return the ID of this entity
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of this entity
     * @param id the new ID
     */
    public void setId(Long id) {
        this.id = id;
    }

}
