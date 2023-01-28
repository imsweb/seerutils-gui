package com.imsweb.seerutilsgui.editor;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

@SuppressWarnings("unused")
public class ActionSelectHome extends TextAction {

    public ActionSelectHome() {
        super("DELETE_CURRENT_LINE");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null) {
            try {
                int caretPos = target.getCaretPosition();
                String txt = target.getText(0, caretPos);

                int count = 0;
                for (int i = txt.length() - 1; i >= 0; i--) {
                    char c = txt.charAt(i);
                    if (c == '\n')
                        break;
                    else
                        count++;
                }

                int startLine = caretPos - count;
                String partialLine = target.getText(startLine, count);

                int indent = 0;
                for (int i = 0; i < partialLine.length(); i++) {
                    if (partialLine.charAt(i) == ' ')
                        indent++;
                    else
                        break;
                }

                int startLineWithoutIndent = startLine + indent;

                // now that we calculated the position, select the text
                int currentStart = target.getSelectionStart();
                int currentEnd = target.getSelectionEnd();

                if (startLineWithoutIndent == caretPos)
                    target.select(startLine, caretPos);
                else if (currentStart == currentEnd)
                    target.select(startLineWithoutIndent, caretPos);
                else if (currentStart == startLine)
                    target.select(startLineWithoutIndent, caretPos);
                else
                    target.select(startLine, caretPos);
            }
            catch (BadLocationException e1) {
                throw new RuntimeException(e1);
            }
        }
    }
}
