/*
 * Copyright (C) 2009 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.table;

import javax.swing.JCheckBox;

public class SeerTableCheckBox extends JCheckBox {

    public SeerTableCheckBox(String command) {
        super();

        this.setOpaque(false);
        this.setActionCommand(command);
    }
}
