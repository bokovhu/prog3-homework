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

package me.bokov.prog3.db.dao;

import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.BaseEntity;
import me.bokov.prog3.service.db.Dao;
import org.slf4j.Logger;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractDao<E extends BaseEntity> implements Dao<E> {

    @Inject
    private Logger logger;

    @Inject
    private Database database;

    protected abstract String getTable();
    protected abstract Class <E> getEntityClass ();
    protected abstract Map<String, String> getColumnMappings();

    @Override
    public List<E> getAll() {

        logger.debug("{}.getAll ()", getClass().getName());

        try (Connection conn = database.openConnection()) {

            StringBuilder sb = new StringBuilder();

            sb.append("SELECT ")
                    .append(String.join(", ", getColumnMappings().keySet()))
                    .append(" FROM ").append(getTable());

            return conn.createQuery(sb.toString())
                    .setColumnMappings(getColumnMappings())
                    .executeAndFetch(getEntityClass());

        }
    }

    @Override
    public E getById(Long id) {

        logger.debug("{}.getById ({})", getClass().getName(), id);

        try (Connection conn = database.openConnection()) {

            StringBuilder sb = new StringBuilder();

            sb.append("SELECT ")
                    .append(String.join(", ", getColumnMappings().keySet()))
                    .append(" FROM ").append(getTable())
                    .append(" WHERE id = :id");

            return conn.createQuery(sb.toString())
                    .setColumnMappings(getColumnMappings())
                    .addParameter("id", id)
                    .executeAndFetch(getEntityClass())
                    .get(0);

        }

    }

    @Override
    public void create(E entity) {

    }

    @Override
    public void update(E entity) {

    }

    @Override
    public void delete(E entity) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
