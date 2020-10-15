/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class SeerProgressPanel extends JPanel {

    private JLabel _topLbl, _firstBottomLbl, _secondBottomLbl;
    private final JProgressBar _progressBar;

    public SeerProgressPanel(int min, int max, int size, String topLbl, String bottomLbl1, String bottomLbl2) {
        this(min, max, size, topLbl, bottomLbl1, bottomLbl2, 20, 20, 10);
    }

    public SeerProgressPanel(int min, int max, int size, String topLbl, String bottomLbl1, String bottomLbl2, int topGap, int bottomGap1, int bottomGap2) {

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        JPanel searchPnl = SeerGuiUtils.createPanel();
        searchPnl.setLayout(new BoxLayout(searchPnl, BoxLayout.Y_AXIS));

        if (topLbl != null) {
            JPanel searchTopPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchTopPnl.setOpaque(false);
            _topLbl = new JLabel(topLbl);
            searchTopPnl.add(_topLbl);
            searchPnl.add(searchTopPnl);
            searchPnl.add(Box.createVerticalStrut(topGap));
        }

        JPanel spinPnl = SeerGuiUtils.createPanel();
        spinPnl.setBorder(BorderFactory.createEmptyBorder());
        spinPnl.setPreferredSize(new Dimension(size, size));
        _progressBar = new JProgressBar();
        if (max != -1) {
            _progressBar.setMinimum(min);
            _progressBar.setMaximum(max);
            _progressBar.setValue(min);
        }
        else
            _progressBar.setIndeterminate(true);
        _progressBar.setStringPainted(false);
        searchPnl.add(_progressBar);

        if (bottomLbl1 != null) {
            searchPnl.add(Box.createVerticalStrut(bottomGap1));
            JPanel searchBottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchBottomPnl.setOpaque(false);
            _firstBottomLbl = new JLabel(bottomLbl1);
            searchBottomPnl.add(_firstBottomLbl);
            searchPnl.add(searchBottomPnl);
        }

        if (bottomLbl2 != null) {
            searchPnl.add(Box.createVerticalStrut(bottomGap2));
            JPanel searchBottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchBottomPnl.setOpaque(false);
            _secondBottomLbl = new JLabel(bottomLbl2);
            searchBottomPnl.add(_secondBottomLbl);
            searchPnl.add(searchBottomPnl);
        }

        JPanel searchWrapperPnl = new JPanel(new GridBagLayout());
        searchWrapperPnl.setOpaque(false);
        searchWrapperPnl.add(searchPnl);

        this.add(searchWrapperPnl, BorderLayout.CENTER);
    }

    public void setProgress(int n) {
        _progressBar.setValue(n);
    }

    public void setTopLabel(String lbl) {
        if (_topLbl != null)
            _topLbl.setText(lbl);
    }

    public void setFirstBottomLabel(String lbl) {
        if (_firstBottomLbl != null)
            _firstBottomLbl.setText(lbl);
    }

    public void setSecondBottomLabel(String lbl) {
        if (_secondBottomLbl != null)
            _secondBottomLbl.setText(lbl);
    }
}