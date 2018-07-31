/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class SeerHelpClickableLabel extends SeerClickableLabel {

    private SeerHelpDialog _dlg;

    private boolean _showUndecorated, _showingUndecorated;

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param label label text
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content the HTML content of the dialog as a string
     */
    public SeerHelpClickableLabel(Window parent, String label, String helpId, String dlgTitle, boolean decorate, String content) {
        this(parent, label, helpId, dlgTitle, decorate, content, SeerGuiUtils.createIcon("help.png"));
    }

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param label label text
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content a URL towards the HTML content of the dialog
     */
    public SeerHelpClickableLabel(Window parent, String label, String helpId, String dlgTitle, boolean decorate, URL content) {
        this(parent, label, helpId, dlgTitle, decorate, content, SeerGuiUtils.createIcon("help.png"));
    }

    /**
     * Constructor.
     * @param parent parent window (used to size and position the help dialog)
     * @param label label text
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content the HTML content of the dialog as a string
     * @param icon the icon to use for the button
     */
    public SeerHelpClickableLabel(Window parent, String label, String helpId, String dlgTitle, boolean decorate, String content, ImageIcon icon) {
        this(parent, label, helpId, dlgTitle, icon);
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
     * @param label label text
     * @param helpId a unique identifier for the help dialog (used to make sure we don't show twice the same dialog)
     * @param dlgTitle the help dialog title (the prefix "Help - " will be added)
     * @param decorate if true, the displayed dialog will have a border
     * @param content a URL towards the HTML content of the dialog
     * @param icon the icon to use for the button
     */
    public SeerHelpClickableLabel(Window parent, String label, String helpId, String dlgTitle, boolean decorate, URL content, ImageIcon icon) {
        this(parent, label, helpId, dlgTitle, icon);
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

    protected SeerHelpClickableLabel(final Window parent, String label, final String helpId, String dlgTitle, ImageIcon icon) {
        super(label);

        this.setName("display-help-clickable-lbl");
        this.setAction(() -> SwingUtilities.invokeLater(() -> {
            if (SeerGuiUtils.show(helpId) == null) {
                if (_showingUndecorated)
                    _dlg.performClose();
                else if (!_showUndecorated) {
                    SeerGuiUtils.showAndPosition(_dlg, parent);
                }
                else {
                    _showingUndecorated = true;
                    _dlg.setRelativeComponent(SeerHelpClickableLabel.this, parent);
                    // there is a bug in Swing where the first call does not size the window properly, but the second call fixes that :-(
                    _dlg.setRelativeComponent(SeerHelpClickableLabel.this, parent);
                    _dlg.setVisible(true);
                }
            }
        }));
    }

    public SeerHelpDialog getDialog() {
        return _dlg;
    }
}
