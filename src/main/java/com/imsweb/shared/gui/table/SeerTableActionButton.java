/*
 * Copyright (C) 2009 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.table;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;

import com.imsweb.shared.gui.SeerGuiUtils;

public class SeerTableActionButton extends JButton {

    public static final int ACTION_BUTTON_SIZE = 28;

    public static final int ACTION_BUTTON_GAP = 4;

    public SeerTableActionButton(String icon, String command) {
        super();

        this.setOpaque(false);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setPreferredSize(new Dimension(ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE));
        this.setMaximumSize(new Dimension(ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE));
        this.setMinimumSize(new Dimension(ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE));
        this.setActionCommand(command);
        this.setFocusPainted(false);
        this.setIcon(SeerGuiUtils.createIcon(icon));
    }
}
