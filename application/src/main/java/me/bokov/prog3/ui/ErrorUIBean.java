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

import me.bokov.prog3.util.I18N;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

@ApplicationScoped
public class ErrorUIBean {

    @Inject
    private I18N i18n;

    public void showErrorMessage(String errorMessage) {

        JOptionPane.showMessageDialog(
                null,
                errorMessage,
                i18n.getText("error"),
                JOptionPane.ERROR_MESSAGE
        );

    }

    public void showThrowable(Throwable throwable) {

        JTextArea stackTraceTextArea = new JTextArea(25, 80);
        stackTraceTextArea.setEditable(false);

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        stackTraceTextArea.setText(sw.toString());

        JOptionPane.showMessageDialog(
                null,
                new JComponent[]{
                        new JLabel(i18n.getText("error-stacktrace")),
                        stackTraceTextArea
                },
                i18n.getText("error"),
                JOptionPane.ERROR_MESSAGE
        );

    }

}
