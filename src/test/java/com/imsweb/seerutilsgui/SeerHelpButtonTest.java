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

        SeerGuiUtils.setFontDelta(2);

        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(600, 200));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPnl = SeerGuiUtils.createPanel();
        contentPnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(contentPnl, BorderLayout.CENTER);

        String text = "This is <b>just</b> a test - <a href=\"https://www.squishlist.com/ims/seerdms_dev/\">Squish</a>!!!";
        //String text = "This is more text contained in this dialog<br/><br/>And this is a second line with also a lot of text...";

        contentPnl.setLayout(new GridBagLayout());
        JButton btn = new SeerHelpButton(frame, contentPnl, "test", "Test", false, text);
        //btn.setEnabled(false);
        contentPnl.add(btn);
        SeerGuiUtils.showAndPosition(frame, null);

    }

}
