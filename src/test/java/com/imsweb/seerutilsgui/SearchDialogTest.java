/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.ArrayUtils;

import com.imsweb.seerutilsgui.editor.ActionFind;
import com.imsweb.seerutilsgui.editor.SearchDialog;
import com.imsweb.seerutilsgui.editor.SyntaxKit;

public class SearchDialogTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(new SyntaxKit(SyntaxKit.SYNTAX_TYPE_PLAIN, null));
        pane.setFont(getMonospaceFont());
        pane.setEditable(false);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 100; i++)
            buf.append("This is line #").append(i).append("\n");
        pane.setText(buf.toString());
        JScrollPane scrollPane = new JScrollPane(pane);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(pane.getFontMetrics(pane.getFont()).getHeight());
        contentPnl.add(scrollPane, BorderLayout.CENTER);

        SearchDialog searchDlg = new SearchDialog(frame, pane);
        searchDlg.setTextComponent(pane);
        registerAction(pane, new ActionFind(searchDlg), KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));

        SeerGuiUtils.showAndPosition(frame, null);
    }

    private static Font getMonospaceFont() {
        if (ArrayUtils.contains(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(), "Courier New"))
            return new Font("Courier New", Font.PLAIN, 13);
        return new Font("Monospaced", Font.PLAIN, 13);
    }

    private static void registerAction(JEditorPane pane, Action action, KeyStroke stroke) {
        pane.getInputMap().put(stroke, "action-" + stroke.toString());
        pane.getActionMap().put("action-" + stroke, action);
        if (action instanceof CaretListener)
            pane.addCaretListener((CaretListener)action);
        if (action instanceof KeyListener)
            pane.addKeyListener((KeyListener)action);
    }
}
