/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.ArrayUtils;

import com.imsweb.seerutilsgui.editor.AutoCompleteDialog;
import com.imsweb.seerutilsgui.editor.SyntaxKit;

public class AutoCompleteDialogTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        SeerGuiUtils.setFontDelta(2);

        JFrame frame = new JFrame("Text Autocompletion Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 400));
        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        JEditorPane editor = new JEditorPane();
        JScrollPane pane = new JScrollPane(editor);
        pane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        contentPnl.add(pane, BorderLayout.CENTER);

        Properties colors = new Properties();
        colors.setProperty("OPERATOR", "0x000000, 0");
        colors.setProperty("KEYWORD", "0x7F0065, 1");
        colors.setProperty("KEYWORD2", "0x7F0065, 1");
        colors.setProperty("TYPE", "0x000000, 0");
        colors.setProperty("TYPE2", "0x000000, 0");
        colors.setProperty("STRING", "0x0026C2, 0");
        colors.setProperty("STRING2", "0x0026C2, 1");
        colors.setProperty("NUMBER", "0x000000, 0");
        colors.setProperty("REGEX", "0x000000, 0");
        colors.setProperty("IDENTIFIER", "0x000000, 0");
        colors.setProperty("COMMENT", "0x227200, 0");
        colors.setProperty("COMMENT2", "0x227200, 0");
        colors.setProperty("DEFAULT", "0x000000, 0");
        editor.setEditorKit(new SyntaxKit(SyntaxKit.SYNTAX_TYPE_GROOVY, colors));

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if (ArrayUtils.contains(fonts, "Courier new"))
            editor.setFont(new Font("Courier New", Font.PLAIN, 12));
        else if (ArrayUtils.contains(fonts, "Courier"))
            editor.setFont(new Font("Courier", Font.PLAIN, 12));
        else
            editor.setFont(new Font("Monospaced", Font.PLAIN, 13));

        editor.setFont(SeerGuiUtils.adjustFontSize(editor.getFont()));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK), "autocomplete");
        editor.getActionMap().put("autocomplete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(
                        () -> new AutoCompleteDialog(null, editor, "env.", new HashSet<>(Arrays.asList("prop1", "prop2", "other")), AutoCompleteDialog.evaluateCurrentWord(editor)).setVisible(true));
            }
        });

        editor.setText("// type 'env.' followed by ctrl-space\r\n// to see the autocompletion dialog...\r\n");
        editor.setCaretPosition(editor.getDocument().getLength());

        SeerGuiUtils.showAndPosition(frame, null);
    }
}
