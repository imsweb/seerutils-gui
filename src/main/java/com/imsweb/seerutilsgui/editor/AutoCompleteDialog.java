/*
 * Copyright (C) 2011 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.imsweb.seerutilsgui.SeerGuiUtils;
import com.imsweb.seerutilsgui.SeerList;
import com.imsweb.seerutilsgui.SeerOptionalSidesLineBorder;

public class AutoCompleteDialog extends JDialog {

    private static final Color _BG_COLOR = new Color(255, 255, 210);
    private static final Color _FG_COLOR = new Color(235, 235, 0);

    protected String _currentWord, _prefix;

    protected JEditorPane _parent;

    protected JTextField _filterFld;

    protected SeerList<String> _list;

    public AutoCompleteDialog(Window owner, JEditorPane parent, String prefix, Set<String> words, String currentWord) {
        super(owner);

        _parent = parent;
        _currentWord = currentWord;
        _prefix = prefix;

        this.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        this.setAlwaysOnTop(true);
        this.setFocusCycleRoot(false);
        this.setUndecorated(true);

        _list = new SeerList<>(new ArrayList<>(words), SeerList.DISPLAY_MODE_NONE, SeerList.FILTERING_MODE_STARTS_WITH, false, String.CASE_INSENSITIVE_ORDER);
        _list.setBackground(_BG_COLOR);
        _list.setSelectionBackground(_FG_COLOR);
        _list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        _list.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCompleteCurrentWord((String)_list.getSelectedValue());
            }
        });
        _list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        _list.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCompleteCurrentWord(null);
            }
        });
        _list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    e.consume();
                    performCompleteCurrentWord((String)_list.getSelectedValue());
                }
            }
        });

        JPanel contentPnl = SeerGuiUtils.createPanel();
        contentPnl.setOpaque(true);
        contentPnl.setBackground(_BG_COLOR);
        contentPnl.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // NORTH - filter
        JPanel filterPnl = SeerGuiUtils.createPanel();
        filterPnl.setBorder(new SeerOptionalSidesLineBorder(Color.GRAY, false, false, true, false));
        _filterFld = new JTextField();
        _filterFld.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        _filterFld.setBackground(_BG_COLOR);

        _filterFld.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        _filterFld.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performHide();
            }
        });

        _filterFld.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        _filterFld.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_list.getSelectedIndex() != -1)
                    performCompleteCurrentWord((String)_list.getSelectedValue());
                else
                    performCompleteCurrentWord(_filterFld.getText());
            }
        });

        _filterFld.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "setFocusOnTable");
        _filterFld.getActionMap().put("setFocusOnTable", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> _list.requestFocusInWindow());
            }
        });

        _filterFld.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    SeerGuiUtils.hideAndDestroy(AutoCompleteDialog.this);
                else {
                    _list.filter(_filterFld.getText());
                    if (_list.getModel().getSize() > 0)
                        _list.setSelectedIndex(0);
                }
            }
        });
        filterPnl.add(_filterFld, BorderLayout.CENTER);
        contentPnl.add(filterPnl, BorderLayout.NORTH);

        // CENTER - words
        JScrollPane scrollPane = new JScrollPane(_list);
        scrollPane.setBorder(null);
        contentPnl.add(scrollPane, BorderLayout.CENTER);

        _filterFld.setText(_currentWord.replace(_prefix, ""));
        _filterFld.setCaretPosition(_filterFld.getText().length());

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(contentPnl, BorderLayout.CENTER);

        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                SeerGuiUtils.hideAndDestroy(AutoCompleteDialog.this);
            }
        });

        // determine position to show this dialog
        this.setPreferredSize(new Dimension(owner == null ? 500 : owner.getWidth() / 2 - 50, 240));
        this.pack();
        int w = this.getPreferredSize().width;
        int h = this.getPreferredSize().height;
        Point point;
        if (_parent.getCaret().getMagicCaretPosition() != null)
            point = (Point)_parent.getCaret().getMagicCaretPosition().clone();
        else
            point = new Point(0, 0);

        boolean displayLeft = point.x + 5 + w >= _parent.getWidth();
        boolean displayTop = point.y + 10 + h >= _parent.getHeight();

        point.x = displayLeft ? point.x - 5 - w : point.x + 5;
        point.y = displayTop ? point.y - h : point.y + 20;

        SwingUtilities.convertPointToScreen(point, _parent);
        this.setLocation(point.x, point.y);
    }

    protected void performCompleteCurrentWord(String word) {
        if (word == null) {
            performHide();
            return;
        }

        int caretPosition = _parent.getCaretPosition();
        Document doc = _parent.getDocument();

        try {
            doc.remove(caretPosition - _currentWord.length(), _currentWord.length());
            caretPosition -= _currentWord.length();
            doc.insertString(caretPosition, _prefix + word, null);
        }
        catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        SeerGuiUtils.hideAndDestroy(AutoCompleteDialog.this);

        _parent.requestFocusInWindow();

        int extraSpace = word.endsWith("()") ? -1 : 0;
        try {
            _parent.setCaretPosition(caretPosition + _prefix.length() + word.length() + extraSpace);
        }
        catch (RuntimeException e) {
            //do nothing
        }
    }

    protected void performHide() {
        SeerGuiUtils.hideAndDestroy(AutoCompleteDialog.this);
    }

    public static String evaluateCurrentWord(JEditorPane pane) {
        StringBuilder buf = new StringBuilder();

        String text = pane.getText().replaceAll("\\r\\n", "\n");
        for (int i = pane.getCaretPosition() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == ' ' || c == '\n' || c == '(' || c == '[' || c == '{')
                break;

            buf.append(c);
        }

        StringBuilder fub = new StringBuilder();
        for (int i = buf.length() - 1; i >= 0; i--)
            fub.append(buf.charAt(i));

        return fub.toString();
    }
}
