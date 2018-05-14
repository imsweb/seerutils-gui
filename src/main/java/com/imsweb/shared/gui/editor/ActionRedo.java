/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

public class ActionRedo extends TextAction {

    public ActionRedo() {
        super("REDO");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null && target.getDocument() instanceof SyntaxDocument) {
            SyntaxDocument sDoc = (SyntaxDocument)target.getDocument();
            sDoc.doRedo();
        }
    }
}
