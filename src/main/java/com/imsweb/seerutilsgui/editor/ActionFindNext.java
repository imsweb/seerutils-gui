/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.TextAction;

@SuppressWarnings("unused")
public class ActionFindNext extends TextAction {

    private final SearchDialog _searchDlg;

    public ActionFindNext(SearchDialog searchDlg) {
        super("FIND_NEXT");

        _searchDlg = searchDlg;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _searchDlg.performFind();
    }

}
