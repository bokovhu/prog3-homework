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
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.I18N;

import javax.inject.Inject;
import javax.swing.*;

public abstract class ScreenBase {

    @Inject
    protected ApplicationUIBean applicationUIBean;

    @Inject
    protected I18N i18n;

    @Inject
    protected Config config;

    protected JPanel panel = null;

    public abstract void initialize();

    public void activate() {

        applicationUIBean.changeContent(panel);

    }

    protected boolean guiEnabled() {
        return !Application.getInstance().getCommandLine().hasOption("no-gui")
                && !Application.getInstance().getCommandLine().hasOption("server");
    }

}
