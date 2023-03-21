/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class SeerSpinningPanelTest {

    private static final List<SeerSpinningPanel> _PNL = new ArrayList<>();

    private static boolean _IS_SPINNING = true;

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        SeerGuiUtils.setFontDelta(2);

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPnl = SeerGuiUtils.createPanel();
        contentPnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(contentPnl, BorderLayout.CENTER);

        JButton btn = SeerGuiUtils.createButton("TOGGLE SPINNING", "action", "tooltip", arg0 -> {
            if (_IS_SPINNING) {
                for (SeerSpinningPanel p : _PNL)
                    p.stopSpinning();
            }
            else {
                for (SeerSpinningPanel p : _PNL)
                    p.startSpinning();
            }
            _IS_SPINNING = !_IS_SPINNING;
        });
        contentPnl.add(btn, BorderLayout.NORTH);

        SeerSpinningPanel pnl = new SeerSpinningPanel(64, "TOP LABEL... ", "BOTTOM1 LABEL... ", "BOTTOM2 LABEL... ");
        _PNL.add(pnl);
        JPanel wrapper = SeerGuiUtils.createPanel();
        wrapper.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        wrapper.add(pnl, BorderLayout.CENTER);
        contentPnl.add(wrapper, BorderLayout.CENTER);

        JPanel p = SeerGuiUtils.createPanel(new FlowLayout());
        int[] sizes = {12, 18, 24, 32, 36, 48, 50};
        for (int size : sizes) {
            pnl = new SeerSpinningPanel(size, null, null, null);
            _PNL.add(pnl);
            p.add(pnl);
        }
        contentPnl.add(p, BorderLayout.SOUTH);

        SeerGuiUtils.showAndPosition(frame, null);
    }
}
