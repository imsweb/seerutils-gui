/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A panel that uses a card layout to display pages on top of each other.
 */
@SuppressWarnings("unused")
public class SeerCardPanel extends JPanel {

    private final CardLayout _layout;

    private String _currentPageId;

    private final Map<String, JComponent> _pages;

    public SeerCardPanel() {
        _layout = new CardLayout();
        _pages = new HashMap<>();

        this.setBorder(null);
        this.setOpaque(false);
        this.setLayout(_layout);
    }

    public void addPage(String id, JComponent page) {
        super.add(id, page);
        _pages.put(id, page);
        if (_currentPageId == null)
            _currentPageId = id;
    }

    public void showPage(String id) {
        _layout.show(this, id);
        _currentPageId = id;
    }

    public String getCurrentPageId() {
        return _currentPageId;
    }

    public JComponent getCurrentPage() {
        return _pages.get(_currentPageId);
    }
}
