/*
 * Copyright (C) 2011 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

@SuppressWarnings("unused")
public class ActionTabToSpace extends TextAction {

    public ActionTabToSpace() {
        super("TAB_TO_SPACE");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        try {
            target.getDocument().insertString(target.getCaretPosition(), "    ", null);
        }
        catch (BadLocationException ble) {
            //ignore
        }
    }
}