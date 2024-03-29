/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

@SuppressWarnings("unused")
public class SeerSpinningPanel extends JPanel {

    private final SeerSpinner _spinner;

    private JLabel _topLbl;
    private JLabel _firstBottomLbl;
    private JLabel _secondBottomLbl;

    public SeerSpinningPanel(int size, String topLbl, String bottomLbl1, String bottomLbl2) {
        this(size, topLbl, bottomLbl1, bottomLbl2, 20, 20, 10);
    }

    public SeerSpinningPanel(int size, String topLbl, String bottomLbl1, String bottomLbl2, int topGap, int bottomGap1, int bottomGap2) {

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        JPanel searchPnl = SeerGuiUtils.createPanel();
        searchPnl.setLayout(new BoxLayout(searchPnl, BoxLayout.Y_AXIS));

        if (topLbl != null) {
            JPanel searchTopPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchTopPnl.setOpaque(false);
            _topLbl = SeerGuiUtils.createLabel(topLbl);
            searchTopPnl.add(_topLbl);
            searchPnl.add(searchTopPnl);
            searchPnl.add(Box.createVerticalStrut(topGap));
        }

        _spinner = new SeerSpinner(size);
        searchPnl.add(_spinner);

        if (bottomLbl1 != null) {
            searchPnl.add(Box.createVerticalStrut(bottomGap1));
            JPanel searchBottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchBottomPnl.setOpaque(false);
            _firstBottomLbl = SeerGuiUtils.createLabel(bottomLbl1);
            searchBottomPnl.add(_firstBottomLbl);
            searchPnl.add(searchBottomPnl);
        }

        if (bottomLbl2 != null) {
            searchPnl.add(Box.createVerticalStrut(bottomGap2));
            JPanel searchBottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchBottomPnl.setOpaque(false);
            _secondBottomLbl = SeerGuiUtils.createLabel(bottomLbl2);
            searchBottomPnl.add(_secondBottomLbl);
            searchPnl.add(searchBottomPnl);
        }

        JPanel searchWrapperPnl = new JPanel(new GridBagLayout());
        searchWrapperPnl.setOpaque(false);
        searchWrapperPnl.add(searchPnl);

        this.add(searchWrapperPnl, BorderLayout.CENTER);

        SwingUtilities.invokeLater(_spinner::startSpinning);
    }

    public void startSpinning() {
        _spinner.startSpinning();
    }

    public void stopSpinning() {
        _spinner.stopSpinning();
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
