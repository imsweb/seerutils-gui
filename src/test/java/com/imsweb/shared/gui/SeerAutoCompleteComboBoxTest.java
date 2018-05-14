/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class SeerAutoCompleteComboBoxTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 300));

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++)
            list.add("Object " + i);
        SeerAutoCompleteComboBox box = new SeerAutoCompleteComboBox(list);
        contentPnl.add(box, BorderLayout.NORTH);

        // just so the combo box can loose focus
        contentPnl.add(new JTextField(), BorderLayout.SOUTH);

        SeerGuiUtils.showAndPosition(frame, null);
    }
}
