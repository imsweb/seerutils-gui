package com.imsweb.shared.gui.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import com.imsweb.shared.gui.SeerGuiUtils;

public class ActionGoToLine extends TextAction {

    private Window _parent;

    public ActionGoToLine(Window parent) {
        super("GO_TO_LINE");

        _parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        int numLines = SyntaxUtils.getSyntaxDocument(target).getNumberOfLines();
        SeerGuiUtils.showAndPosition(new GoToLineDialog(_parent, target, numLines), _parent);
    }
}
