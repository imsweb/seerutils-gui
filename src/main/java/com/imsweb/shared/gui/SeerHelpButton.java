/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class SeerHelpButton extends JButton {

    private SeerHelpDialog _dlg;

    private boolean _showUndecorated, _showingUndecorated;

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param ancestor parent container (used to set the cursor on mouse over)
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content the HTML content of the dialog as a string
     */
    public SeerHelpButton(Window parent, JComponent ancestor, String helpId, String dlgTitle, boolean decorate, String content) {
        this(parent, ancestor, helpId, dlgTitle, decorate, content, SeerGuiUtils.createIcon("help.png"));
    }

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param ancestor parent container (used to set the cursor on mouse over)
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content a URL towards the HTML content of the dialog
     */
    public SeerHelpButton(Window parent, JComponent ancestor, String helpId, String dlgTitle, boolean decorate, URL content) {
        this(parent, ancestor, helpId, dlgTitle, decorate, content, SeerGuiUtils.createIcon("help.png"));
    }

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param ancestor parent container (used to set the cursor on mouse over)
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content the HTML content of the dialog as a string
     * @param icon the icon to use for the button
     */
    public SeerHelpButton(Window parent, JComponent ancestor, String helpId, String dlgTitle, boolean decorate, String content, ImageIcon icon) {
        this(parent, ancestor, helpId, dlgTitle, icon);
        _dlg = new SeerHelpDialog(parent, helpId, "Help - " + dlgTitle, icon.getImage(), content, decorate);
        _dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                _showingUndecorated = false;
            }
        });
        _showUndecorated = !decorate;
        _showingUndecorated = false;
    }

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param ancestor parent container (used to set the cursor on mouse over)
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content a URL towards the HTML content of the dialog
     * @param icon the icon to use for the button
     */
    public SeerHelpButton(Window parent, JComponent ancestor, String helpId, String dlgTitle, boolean decorate, URL content, ImageIcon icon) {
        this(parent, ancestor, helpId, dlgTitle, icon);
        _dlg = new SeerHelpDialog(parent, helpId, "Help - " + dlgTitle, icon.getImage(), content);
        _dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                _showingUndecorated = false;
            }
        });
        _showUndecorated = !decorate;
        _showingUndecorated = false;
    }

    protected SeerHelpButton(final Window parent, final JComponent ancestor, final String helpId, String dlgTitle, ImageIcon icon) {

        this.setOpaque(false);
        this.setActionCommand("display-help");
        this.setName("display-help-btn");
        this.setToolTipText(null); // the help dialog already behave like a tooltip, having both is just weird
        this.setIcon(icon);
        this.setContentAreaFilled(false);
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent arg0) {
                _dlg.setMouseOnButton(true);
                if (SeerHelpButton.this.isEnabled()) {
                    if (ancestor != null)
                        ancestor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else
                        parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
                _dlg.setMouseOnButton(false);
                if (SeerHelpButton.this.isEnabled()) {
                    if (ancestor != null)
                        ancestor.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    else
                        parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        this.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            if (SeerGuiUtils.show(helpId) == null) {
                if (_showingUndecorated)
                    _dlg.performClose();
                else if (!_showUndecorated) {
                    SeerGuiUtils.showAndPosition(_dlg, parent);
                }
                else {
                    _showingUndecorated = true;
                    _dlg.setRelativeComponent((JComponent)e.getSource(), parent);
                    // there is a bug in Swing where the first call does not size the window properly, but the second call fixes that :-(
                    _dlg.setRelativeComponent((JComponent)e.getSource(), parent);
                    _dlg.setVisible(true);
                }
            }
        }));
    }

    public SeerHelpDialog getDialog() {
        return _dlg;
    }
}
