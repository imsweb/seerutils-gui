/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class SeerBoldTitlesTabbedPaneHeader extends JPanel {

    protected JLabel _tabHeaderLbl;
    protected JLabel _iconLbl;

    public SeerBoldTitlesTabbedPaneHeader(String label) {
        this(label, null, BorderFactory.createEmptyBorder(2, 4, 2, 4), false, false);
    }

    public SeerBoldTitlesTabbedPaneHeader(String label, ImageIcon icon) {
        this(label, icon, BorderFactory.createEmptyBorder(2, 4, 2, 4), false, false);
    }

    public SeerBoldTitlesTabbedPaneHeader(String label, ImageIcon icon, Border titleBorder, boolean centerTitle) {
        this(label, icon, titleBorder, centerTitle, false);
    }

    public SeerBoldTitlesTabbedPaneHeader(String label, ImageIcon icon, Border titleBorder, boolean centerTitle, boolean makeCurrent) {
        this.setLayout(new FlowLayout(centerTitle ? FlowLayout.CENTER : FlowLayout.LEADING, 0, 0));
        this.setOpaque(false);
        if (titleBorder != null)
            this.setBorder(titleBorder);

        if (icon != null) {
            _iconLbl = new JLabel(icon);
            this.add(_iconLbl);
            this.add(Box.createHorizontalStrut(8));
        }

        _tabHeaderLbl = SeerGuiUtils.createLabel(label);
        this.add(_tabHeaderLbl);

        if (makeCurrent)
            _tabHeaderLbl.setFont(_tabHeaderLbl.getFont().deriveFont(Font.BOLD));
    }

    public void setCurrent(boolean current) {
        if (current)
            _tabHeaderLbl.setFont(_tabHeaderLbl.getFont().deriveFont(Font.BOLD));
        else
            _tabHeaderLbl.setFont(_tabHeaderLbl.getFont().deriveFont(Font.PLAIN));
    }

    public void updateHeader(String text) {
        _tabHeaderLbl.setText(text);
    }

    public String getHeaderTitle() {
        return _tabHeaderLbl.getText();
    }

    public void disableHeader() {
        _tabHeaderLbl.setEnabled(false);
        if (_iconLbl != null)
            _iconLbl.setEnabled(false);
    }

    public void enableHeader() {
        _tabHeaderLbl.setEnabled(true);
        if (_iconLbl != null)
            _iconLbl.setEnabled(true);
    }

    public boolean isHeaderEnabled() {
        return _tabHeaderLbl.isEnabled();
    }

}
