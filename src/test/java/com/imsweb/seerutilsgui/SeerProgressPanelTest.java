/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SeerProgressPanelTest {

    private static int _PROGRESS;
    private static SeerProgressPanel _PROGRESS_PNL;

    private static int updateProgress() {
        _PROGRESS += 2;
        if (_PROGRESS == 10)
            _PROGRESS_PNL.setTopLabel("Hooray!");
        else if (_PROGRESS > 10) {
            _PROGRESS_PNL.setTopLabel("Progress Panel");
            _PROGRESS = 0;
        }
        return _PROGRESS;
    }

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();
        JFrame myFrame = new JFrame("SeerProgressPanelTest");
        myFrame.setLayout(new BorderLayout());
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setPreferredSize(new Dimension(300, 200));

        SeerGuiUtils.setFontDelta(2);

        _PROGRESS_PNL = new SeerProgressPanel(0, 10, 100, "Waiting to start", "", "", 20, 0, 0);
        myFrame.add(_PROGRESS_PNL, BorderLayout.CENTER);

        JPanel btnPnl = SeerGuiUtils.createPanel();
        btnPnl.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        btnPnl.add(SeerGuiUtils.createButton("Set Labels", "set-labels", null, e -> {
            _PROGRESS_PNL.setTopLabel("Progress Panel");
            _PROGRESS_PNL.setFirstBottomLabel("First Bottom Label");
            _PROGRESS_PNL.setSecondBottomLabel("Second Bottom Label");
        }));
        btnPnl.add(SeerGuiUtils.createButton("Update progress", "update-progress", null, e -> _PROGRESS_PNL.setProgress(updateProgress())));
        myFrame.add(btnPnl, BorderLayout.SOUTH);

        SeerGuiUtils.showAndPosition(myFrame, null);
    }
}