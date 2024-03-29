/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

@SuppressWarnings("unused")
public class ActionUndo extends TextAction {

    public ActionUndo() {
        super("UNDO");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null && target.getDocument() instanceof SyntaxDocument) {
            SyntaxDocument sDoc = (SyntaxDocument)target.getDocument();
            sDoc.doUndo();
        }
    }
}
