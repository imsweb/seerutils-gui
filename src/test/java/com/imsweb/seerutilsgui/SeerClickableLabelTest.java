/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SeerClickableLabelTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(500, 200));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);
        contentPnl.setLayout(new GridBagLayout());

        SeerClickableLabel lbl = new SeerClickableLabel("Click me and enjoy Google!", SeerClickableLabel.createUrlAction("www.google.com", Action.BROWSE));
        //SeerHelpClickableLabel lbl = new SeerHelpClickableLabel(frame, "Click me for some help!", "help", "Help", false, "<h1>HELP!!!</h1>");

        lbl.setEnabled(true);
        contentPnl.add(lbl);
        SeerGuiUtils.showAndPosition(frame, null);

    }

}
