package com.imsweb.seerutilsgui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

@SuppressWarnings("unused")
public class ActionDeleteCurrentLine extends TextAction {

    public ActionDeleteCurrentLine() {
        super("DELETE_CURRENT_LINE");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null) {
            try {
                SyntaxUtils.getSyntaxDocument(target).removeLineAt(target.getCaretPosition());
            }
            catch (BadLocationException e1) {
                throw new RuntimeException(e1);
            }
        }
    }
}
