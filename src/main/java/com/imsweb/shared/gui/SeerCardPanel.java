/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A panel that uses a card layout to display pages on top of each other.
 */
public class SeerCardPanel extends JPanel {

    private CardLayout _layout;

    private String _currentPageId;

    public SeerCardPanel() {

        _layout = new CardLayout();

        this.setBorder(null);
        this.setOpaque(false);
        this.setLayout(_layout);
    }

    public void addPage(String id, JComponent page) {
        this.add(id, page);
        if (_currentPageId == null)
            _currentPageId = id;
    }

    public void showPage(String id) {
        _layout.show(this, id);
        _currentPageId = id;
    }

    public String getCurrentPage() {
        return _currentPageId;
    }
}
