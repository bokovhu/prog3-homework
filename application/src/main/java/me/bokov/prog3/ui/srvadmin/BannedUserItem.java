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

package me.bokov.prog3.ui.srvadmin;

import com.j256.ormlite.dao.Dao;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatUserEntity;
import me.bokov.prog3.ui.ErrorUIBean;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.*;

public class BannedUserItem extends JPanel {

    private final String username;

    private void initPanel () {

        JLabel usernameLabel = new JLabel(username);
        JButton unbanButton = new JButton("Unban");

        unbanButton.setBackground(Color.GREEN);
        unbanButton.setForeground(Color.WHITE);

        setLayout(new FlowLayout());

        add(usernameLabel);
        add(unbanButton);

        unbanButton.addActionListener(
                e -> {

                    try {

                        Dao<ChatUserEntity, Long> chatUserDao = CDI.current().select(Database.class).get().getChatUserDao();

                        ChatUserEntity chatUserEntity = chatUserDao.queryForEq("username", username).get(0);

                        chatUserEntity.setBanState("NOT_BANNED");

                        chatUserDao.update(chatUserEntity);

                    } catch (Exception exc) {

                        CDI.current().select(ErrorUIBean.class).get().showThrowable(exc);

                    }

                }
        );

    }

    public BannedUserItem(String username) {
        this.username = username;
        initPanel();
    }

}
