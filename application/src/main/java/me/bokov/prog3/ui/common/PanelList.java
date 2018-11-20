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

package me.bokov.prog3.ui.common;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelList <PanelType extends JPanel> extends JPanel {

    private List <PanelType> panels = new ArrayList<>();

    public PanelList () {

        setLayout(new GridLayout(0, 1, 4, 4));

    }

    private void updateLayout () {

        setLayout(new GridLayout(panels.size(), 1));
        removeAll();
        panels.forEach(this::add);

    }

    public void addPanel (PanelType panel) {

        panels.add(panel);
        updateLayout();

    }

    public void removePanel (PanelType panel) {

        panels.remove(panel);
        updateLayout();

    }

    public void removePanel (int index) {

        panels.remove(index);
        updateLayout();

    }

    public int getPanelCount () {
        return panels.size();
    }

    public void clearPanels() {

        panels.clear();
        updateLayout();

    }

}
