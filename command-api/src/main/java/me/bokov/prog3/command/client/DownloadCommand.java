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

package me.bokov.prog3.command.client;

import me.bokov.prog3.command.Command;

/**
 * Represents a {@code DOWNLOAD} command.
 *
 * This command can be used to download a file or image from the server, identified via its {@code file_id}.
 */
public interface DownloadCommand extends Command {

    int SUCCESS = 200;
    int FILE_ID_REQUIRED = 400;
    int INVALID_FILE_ID = 401;

    /**
     * Sets the file ID in the command.
     * @param fileId the file ID to download
     * @return this command instance
     */
    DownloadCommand fileId (String fileId);

}
