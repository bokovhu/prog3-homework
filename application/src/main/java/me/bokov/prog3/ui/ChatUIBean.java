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

import me.bokov.prog3.ui.chat.ChatRoomTab;
import me.bokov.prog3.ui.chat.MessageComposerPanel;

import javax.enterprise.context.ApplicationScoped;
import javax.swing.*;
import java.awt.*;

@ApplicationScoped
public class ChatUIBean extends ScreenBase {

    JTabbedPane chatRoomsTabbedPane;
    MessageComposerPanel messageComposerPanel;

    private void initChatRoomTabs () {

        chatRoomsTabbedPane = new JTabbedPane();
        chatRoomsTabbedPane.addTab("Lobby", new ChatRoomTab());

    }

    private void initMessageComposer () {

        messageComposerPanel = new MessageComposerPanel();

    }

    @Override
    public void initialize() {

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        initChatRoomTabs();
        initMessageComposer();

        GridBagConstraints tabsGbc = new GridBagConstraints();
        tabsGbc.gridx = 0;
        tabsGbc.gridy = 0;
        tabsGbc.gridwidth = 1;
        tabsGbc.gridheight = 1;
        tabsGbc.weightx = 1.0;
        tabsGbc.weighty = 1.0;
        tabsGbc.fill = GridBagConstraints.BOTH;

        panel.add(chatRoomsTabbedPane, tabsGbc);


        GridBagConstraints composerGbc = new GridBagConstraints();
        composerGbc.gridx = 0;
        composerGbc.gridy = 1;
        composerGbc.gridwidth = 1;
        composerGbc.gridheight = 1;
        composerGbc.weightx = 1.0;
        composerGbc.weighty = 0.0;
        composerGbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(messageComposerPanel, composerGbc);

    }

}
