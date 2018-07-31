/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SeerHelpButtonTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(600, 200));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPnl = SeerGuiUtils.createPanel();
        contentPnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(contentPnl, BorderLayout.CENTER);

        contentPnl.setLayout(new GridBagLayout());
        JButton btn = new SeerHelpButton(frame, contentPnl, "test", "Test", false, "This is <b>just</b> a test!!!");
        //btn.setEnabled(false);
        contentPnl.add(btn);
        SeerGuiUtils.showAndPosition(frame, null);

    }

}
