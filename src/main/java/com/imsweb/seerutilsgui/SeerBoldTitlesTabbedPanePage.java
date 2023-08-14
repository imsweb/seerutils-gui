/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class SeerBoldTitlesTabbedPanePage extends JPanel {

    /**
     * Constructor.
     * @param parent parent component
     */
    public SeerBoldTitlesTabbedPanePage(SeerBoldTitlesTabbedPane parent) {
        this(parent, Color.GRAY);
    }

    /**
     * Constructor.
     * @param parent parent component
     * @param borderColor border color
     */
    @SuppressWarnings("unused")
    public SeerBoldTitlesTabbedPanePage(SeerBoldTitlesTabbedPane parent, Color borderColor) {
        super(new BorderLayout());

        this.setOpaque(true);
        this.setBorder(BorderFactory.createLineBorder(borderColor));
    }

    @Override
    public void setBorder(Border border) {
        super.setBorder(new CompoundBorder(getBorder(), border));
    }

}
