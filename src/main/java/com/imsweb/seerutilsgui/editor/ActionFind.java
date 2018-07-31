/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.TextAction;

import com.imsweb.seerutilsgui.SeerGuiUtils;

public class ActionFind extends TextAction {

    private SearchDialog _searchDlg;

    public ActionFind(SearchDialog searchDlg) {
        super("FIND");

        _searchDlg = searchDlg;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!_searchDlg.isVisible())
            SeerGuiUtils.showAndPosition(_searchDlg, null, false, true);
        else if (!_searchDlg.isActive())
            _searchDlg.toFront();

        javax.swing.SwingUtilities.invokeLater(() -> _searchDlg.reApplyFocus());
    }
}
