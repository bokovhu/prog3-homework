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

public class InputGroup extends JPanel {

    private final String label;
    private final Component component;

    public InputGroup(String label, Component component) {
        this.label = label;
        this.component = component;
        initPanel();
    }

    private void initPanel() {

        setLayout(new GridLayout(1, 2, 4, 4));

        add(new JLabel(this.label));
        add(component);

    }
}
