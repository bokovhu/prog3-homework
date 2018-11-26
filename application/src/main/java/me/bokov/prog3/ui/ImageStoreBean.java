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

package me.bokov.prog3.ui;

import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.util.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@ApplicationScoped
public class ImageStoreBean {

    @Inject
    private Config config;

    @Inject
    private ChatClient chatClient;

    public ImageIcon getImageIconByFileId(String fileId, String extension) {

        File imageFile = new File(config.getImageStoreDirectory(), fileId + "." + extension);

        if (!imageFile.exists()) {

            Response downloadResponse = chatClient.getServerEndpoint()
                    .download().fileId(fileId)
                    .execute();

            try (FileOutputStream fos = new FileOutputStream(imageFile)) {

                fos.write(
                        Base64.getDecoder()
                                .decode(
                                        downloadResponse.getData()
                                                .asJsonObject()
                                                .getString("content")
                                )
                );

            } catch (Exception exc) {
                throw new IllegalStateException(exc);
            }

        }

        return new ImageIcon(imageFile.getAbsolutePath());

    }

}
