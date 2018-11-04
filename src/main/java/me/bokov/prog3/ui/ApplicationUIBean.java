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
import java.awt.*;

@ApplicationScoped
public class ApplicationUIBean {

    private static final String WINDOW_TITLE_KEY = "window.title";
    private static final Dimension WINDOW_MIN_SIZE = new Dimension(400, 300);
    private static final Dimension WINDOW_PREF_SIZE = new Dimension(1024, 768);

    private boolean initialized = false;
    private JFrame applicationFrame;

    @Inject
    private I18N i18n;

    @Inject
    private WelcomeUIBean welcomeUIBean;

    private void createApplicationFrame() {

        applicationFrame = new JFrame();

        applicationFrame.setTitle(i18n.getText(WINDOW_TITLE_KEY));

        applicationFrame.setMinimumSize(WINDOW_MIN_SIZE);
        applicationFrame.setPreferredSize(WINDOW_PREF_SIZE);

        applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationFrame.setResizable(true);

    }

    public void initialize() {

        if (!initialized) {

            createApplicationFrame();

            welcomeUIBean.initialize();
            welcomeUIBean.activate();

            applicationFrame.setVisible(true);

            initialized = true;

        } else {

            throw new IllegalStateException("Already initialized!");

        }

    }

    public JFrame getApplicationFrame() {
        return applicationFrame;
    }

    public void changeContent(JPanel content) {

        SwingUtilities.invokeLater(
                () -> {
                    applicationFrame.setContentPane(content);
                    applicationFrame.invalidate();
                }
        );

    }

}
