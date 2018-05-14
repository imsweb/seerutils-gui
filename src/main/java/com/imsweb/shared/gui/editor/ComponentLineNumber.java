/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imsweb.shared.gui.editor;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

public class ComponentLineNumber extends JComponent implements ComponentSyntax, CaretListener {

    public static final int MARGIN = 5;

    private JEditorPane _pane;

    private String _format;

    @Override
    protected void paintComponent(Graphics g) {
        g.setFont(_pane.getFont().deriveFont((float)11));
        Rectangle clip = g.getClipBounds();
        int lh = getLineHeight();
        int end = clip.y + clip.height + lh;
        int lineNum = clip.y / lh + 1;
        // round the start to a multiple of lh, and shift by 2 pixels to align
        // properly to the text.
        for (int y = (clip.y / lh) * lh + lh - 2; y <= end; y += lh) {
            String text = String.format(_format, lineNum);
            lineNum++;
            g.drawString(text, MARGIN, y);
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        updateSize();
    }

    @Override
    public void install(JEditorPane editor, Properties prop) {
        this._pane = editor;
        JScrollPane sp = getScrollPane(_pane);
        if (sp != null) {
            sp.setRowHeaderView(this);
            this._pane.addCaretListener(this);
            updateSize();
        }
    }

    @Override
    public void deinstall(JEditorPane editor) {
        JScrollPane sp = getScrollPane(editor);
        if (sp != null) {
            editor.removeCaretListener(this);
            sp.setRowHeaderView(null);
        }
    }

    private void updateSize() {
        int lineCount = SyntaxUtils.getLineCount(_pane);
        int h = lineCount * getLineHeight() + _pane.getHeight();
        int d = (int)Math.log10(lineCount) + 2;
        if (d < 0) {
            d = 2;
        }
        int w = d * getCharWidth() + 2 * MARGIN;
        _format = "%" + d + "d";
        setPreferredSize(new Dimension(w, h));
    }

    private int getLineHeight() {
        return _pane.getFontMetrics(_pane.getFont()).getHeight();
    }

    private int getCharWidth() {
        return _pane.getFontMetrics(_pane.getFont()).charWidth('0');
    }

    private JScrollPane getScrollPane(JTextComponent editorPane) {
        Container p = editorPane.getParent();
        while (p != null) {
            if (p instanceof JScrollPane) {
                return (JScrollPane)p;
            }
            p = p.getParent();
        }
        return null;
    }
}
