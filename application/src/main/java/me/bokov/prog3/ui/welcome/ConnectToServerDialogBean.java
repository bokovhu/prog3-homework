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

package me.bokov.prog3.ui.welcome;

import me.bokov.prog3.ui.common.InputGroup;
import me.bokov.prog3.util.I18N;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;

@ApplicationScoped
public class ConnectToServerDialogBean {

    @Inject
    private I18N i18n;

    public void show() {

        SwingUtilities.invokeLater(
                () -> {

                    JTextField serverHostnameTextField = new JTextField(40);
                    JFormattedTextField serverPortTextField = new JFormattedTextField();
                    serverPortTextField.setColumns(5);
                    JTextField usernameTextField = new JTextField(40);

                    int connectToServerResult = JOptionPane.showConfirmDialog(
                            null,
                            new Object[]{
                                    new InputGroup(i18n.getText("connect-to-server.server-hostname"), serverHostnameTextField),
                                    new InputGroup(i18n.getText("connect-to-server.server-port"), serverPortTextField),
                                    new InputGroup(i18n.getText("connect-to-server.username"), usernameTextField)
                            },
                            i18n.getText("connect-to-server.dialog-title"),
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (connectToServerResult == JOptionPane.OK_OPTION) {


                    }

                }
        );

    }

}
