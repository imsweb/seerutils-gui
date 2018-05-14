/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.TextAction;

public class ActionFindNext extends TextAction {

    private SearchDialog _searchDlg;

    public ActionFindNext(SearchDialog searchDlg) {
        super("FIND_NEXT");

        _searchDlg = searchDlg;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _searchDlg.performFind();
    }

}
