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

import me.bokov.prog3.Application;
import me.bokov.prog3.ui.welcome.ConnectToServerDialogBean;
import me.bokov.prog3.ui.welcome.StartNewServerDialogBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;

@ApplicationScoped
public class WelcomeUIBean extends ScreenBase {

    private JButton connectToServerButton;
    private JButton startNewServerButton;

    @Inject
    private StartNewServerDialogBean startNewServerDialogBean;

    @Inject
    private ConnectToServerDialogBean connectToServerDialogBean;

    private void createStartNewServerButton() {

        startNewServerButton = new JButton(i18n.getText("welcome.start-new-server"));

        startNewServerButton.addActionListener(
                e -> startNewServerDialogBean.show()
        );

    }

    private void createConnectToServerButton() {

        connectToServerButton = new JButton(i18n.getText("welcome.connect-to-server"));

        connectToServerButton.addActionListener(
                e -> connectToServerDialogBean.show()
        );

    }

    public void initialize() {

        panel = new JPanel();

        panel.add(new JLabel(i18n.getText("welcome.title")));

        createStartNewServerButton();
        createConnectToServerButton();

        panel.add(connectToServerButton);

        if (!Application.getInstance().getCommandLine().hasOption("no-database")) {
            panel.add(startNewServerButton);
        }

    }

}
