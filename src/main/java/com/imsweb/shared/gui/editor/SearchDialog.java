/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.MutableComboBoxModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import com.imsweb.shared.gui.SeerGuiUtils;
import com.imsweb.shared.gui.SeerWindow;

public class SearchDialog extends JDialog implements ActionListener, SeerWindow {

    /**
     * Global GUI components
     */
    private JComboBox _searchBox, _replaceBox;
    private JCheckBox _caseBox, _wrapBox, _regexBox;
    private JRadioButton _allBtn, _selectedBtn;
    private JButton _findBtn, _replaceFindBtn, _replaceBtn, _replaceAllBtn, _countBtn, _closeBtn;
    private JLabel _statusLbl;

    /**
     * Pattern to search for
     */
    private Pattern _pattern = null;

    /**
     * Parent text component
     */
    private JTextComponent _comp;

    /**
     * Marker
     */
    private SyntaxUtils.SimpleMarker _marker = new SyntaxUtils.SimpleMarker(Color.LIGHT_GRAY);

    /**
     * Constructor.
     * <p/>
     * Created on Apr 23, 2010 by Fabian
     */
    public SearchDialog() {
        this(SeerGuiUtils.COLOR_APPLICATION_BACKGROUND);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Apr 23, 2010 by Fabian
     * @param backgroundColor background color
     */
    public SearchDialog(Color backgroundColor) {
        super();

        this.setTitle("Search");
        this.setModal(false);
        this.setResizable(false);
        this.setName("conf-file-editor-search-dlg");
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performClose();
            }
        });

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    performClose();
            }
        };

        JPanel contentPnl = SeerGuiUtils.createPanel();
        contentPnl.setLayout(new BoxLayout(contentPnl, BoxLayout.Y_AXIS));
        contentPnl.setOpaque(true);
        contentPnl.setBackground(backgroundColor);
        contentPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel searchPnl = SeerGuiUtils.createPanel();
        searchPnl.setOpaque(true);
        searchPnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchPnl.setLayout(new BoxLayout(searchPnl, BoxLayout.Y_AXIS));

        JPanel comboPnl = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(4, 4, 4, 4);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        comboPnl.add(new JLabel("Find:"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        _searchBox = new JComboBox();
        _searchBox.setEditable(true);
        _searchBox.setPreferredSize(new Dimension(175, 20));
        ((JTextField)_searchBox.getEditor().getEditorComponent()).getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        ((JTextField)_searchBox.getEditor().getEditorComponent()).getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performFind();
            }
        });
        _searchBox.getEditor().getEditorComponent().addKeyListener(keyAdapter);
        comboPnl.add(_searchBox, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        comboPnl.add(new JLabel("Replace With:"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        _replaceBox = new JComboBox();
        _replaceBox.setEditable(true);
        _replaceBox.setPreferredSize(new Dimension(175, 20));
        ((JTextField)_replaceBox.getEditor().getEditorComponent()).getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        ((JTextField)_replaceBox.getEditor().getEditorComponent()).getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performReplace();
            }
        });
        _replaceBox.getEditor().getEditorComponent().addKeyListener(keyAdapter);
        comboPnl.add(_replaceBox, c);

        searchPnl.add(comboPnl);
        searchPnl.add(Box.createVerticalStrut(10));

        JPanel optionsPnl = SeerGuiUtils.createPanel();
        optionsPnl.setLayout(new BoxLayout(optionsPnl, BoxLayout.Y_AXIS));
        optionsPnl.setBorder(BorderFactory.createTitledBorder("Options"));
        _caseBox = SeerGuiUtils.createCheckBox("Case Sensitive", "option-sensitive", null);
        optionsPnl.add(_caseBox);
        _wrapBox = SeerGuiUtils.createCheckBox("Wrap Search", "option-sensitive", null);
        _wrapBox.setSelected(true);
        optionsPnl.add(_wrapBox);
        _regexBox = SeerGuiUtils.createCheckBox("Regular Expression", "option-sensitive", null);
        optionsPnl.add(_regexBox);
        optionsPnl.add(Box.createHorizontalGlue());

        JPanel scopePnl = SeerGuiUtils.createPanel();
        scopePnl.setLayout(new BoxLayout(scopePnl, BoxLayout.Y_AXIS));
        scopePnl.setBorder(BorderFactory.createTitledBorder("Scope"));
        _allBtn = SeerGuiUtils.createRadioButton("All", "scope-all", null);
        _allBtn.setSelected(true);
        scopePnl.add(_allBtn);
        _selectedBtn = SeerGuiUtils.createRadioButton("Selected Text", "scope-selected", null);
        scopePnl.add(_selectedBtn);
        ButtonGroup group = new ButtonGroup();
        group.add(_allBtn);
        group.add(_selectedBtn);

        JPanel optionsAndScopePnl = SeerGuiUtils.createPanel();
        optionsAndScopePnl.setLayout(new BoxLayout(optionsAndScopePnl, BoxLayout.X_AXIS));
        optionsAndScopePnl.add(optionsPnl);
        JPanel scopeWrapperPnl = SeerGuiUtils.createPanel();
        scopeWrapperPnl.add(scopePnl, BorderLayout.NORTH);
        optionsAndScopePnl.add(scopeWrapperPnl);
        searchPnl.add(optionsAndScopePnl);

        contentPnl.add(searchPnl);

        // SOUTH - controls
        JPanel controlsPnl = SeerGuiUtils.createPanel();
        controlsPnl.setLayout(new BoxLayout(controlsPnl, BoxLayout.Y_AXIS));
        controlsPnl.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel controls1Pnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        _findBtn = SeerGuiUtils.createButton("Find", "find", "Find", this);
        _findBtn.addKeyListener(keyAdapter);
        controls1Pnl.add(_findBtn);
        controls1Pnl.add(Box.createHorizontalStrut(25));
        _replaceFindBtn = SeerGuiUtils.createButton("Replace/Find", "replace-find", "Replace & Find", this);
        _replaceFindBtn.addKeyListener(keyAdapter);
        controls1Pnl.add(_replaceFindBtn);
        controlsPnl.add(controls1Pnl);

        JPanel controls2Pnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        _replaceBtn = SeerGuiUtils.createButton("Replace", "replace", "Replace", this);
        _replaceBtn.addKeyListener(keyAdapter);
        controls2Pnl.add(_replaceBtn);
        controls2Pnl.add(Box.createHorizontalStrut(25));
        _replaceAllBtn = SeerGuiUtils.createButton("Replace All", "replace-all", "Replace All", this);
        _replaceAllBtn.addKeyListener(keyAdapter);
        controls2Pnl.add(_replaceAllBtn);
        controlsPnl.add(controls2Pnl);

        JPanel controls3Pnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        _countBtn = SeerGuiUtils.createButton("Count", "count", "Count", this);
        _countBtn.addKeyListener(keyAdapter);
        controls3Pnl.add(_countBtn);
        controls3Pnl.add(Box.createHorizontalStrut(25));
        _closeBtn = SeerGuiUtils.createButton("Close", "close", "Close", this);
        _closeBtn.addKeyListener(keyAdapter);
        controls3Pnl.add(_closeBtn);
        controlsPnl.add(controls3Pnl);

        SeerGuiUtils.synchronizedComponentsWidth(_findBtn, _replaceFindBtn, _replaceBtn, _replaceAllBtn, _countBtn, _closeBtn);
        contentPnl.add(controlsPnl);

        JPanel statusPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        statusPnl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        _statusLbl = SeerGuiUtils.createLabel(" ");
        statusPnl.add(_statusLbl);
        contentPnl.add(statusPnl);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(contentPnl, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                reApplyFocus();
                SearchDialog.this.removeComponentListener(this);
            }
        });
    }

    public void setTextComponent(JTextComponent comp) {
        _comp = comp;
    }

    public void reApplyFocus() {
        if (_searchBox.getItemCount() > 0) {
            _searchBox.setSelectedIndex(0);
            ((JTextField)_searchBox.getEditor().getEditorComponent()).selectAll();
        }
        _replaceBox.setSelectedIndex(-1);
        _searchBox.requestFocusInWindow();
    }

    public void performFind() {
        updatePattern();
        _statusLbl.setText(" ");
        boolean found = doFind();
        if (!found)
            _statusLbl.setText("Text not found");
    }

    public void performReplaceFind() {
        updatePattern();
        _statusLbl.setText(" ");
        int count = doReplace(false);
        boolean found = doFind();
        if (count > 0) {
            if (found)
                _statusLbl.setText("Text replaced once");
            else
                _statusLbl.setText("Text replaced once; next occurence not found");
        }
        else
            _statusLbl.setText("Text not found");
    }

    public void performReplace() {
        updatePattern();
        _statusLbl.setText(" ");
        int count = doReplace(false);
        if (count == 0)
            _statusLbl.setText("Text not found");
        else
            _statusLbl.setText("Text replaced once");
    }

    public void performReplaceAll() {
        updatePattern();
        _statusLbl.setText(" ");
        int count = doReplace(true);
        if (count == 0)
            _statusLbl.setText("Text not found");
        else if (count == 1)
            _statusLbl.setText("Text replaced once");
        else if (count == 2)
            _statusLbl.setText("Text replaced twice");
        else
            _statusLbl.setText("Text replaced " + count + " times");
    }

    public void performCount() {
        updatePattern();
        _statusLbl.setText(" ");
        int count = doCount();
        if (count == 0)
            _statusLbl.setText("Text not found");
        else if (count == 1)
            _statusLbl.setText("Text found once");
        else if (count == 2)
            _statusLbl.setText("Text found twice");
        else
            _statusLbl.setText("Text found " + count + " times");
    }

    public void performClose() {
        _statusLbl.setText(" ");
        SeerGuiUtils.hide(this, null);
    }

    private boolean doFind() {
        SyntaxDocument sDoc = SyntaxUtils.getSyntaxDocument(_comp);
        if (_pattern == null || sDoc == null)
            return false;

        int start = _comp.getSelectionEnd();
        if (_selectedBtn.isSelected())
            start = _comp.getSelectionStart();

        boolean found = false;
        Matcher matcher = sDoc.getMatcher(_pattern, start);
        if (matcher.find() && (_allBtn.isSelected() || matcher.end() + start < _comp.getSelectionEnd())) {
            found = true;
            _comp.select(matcher.start() + start, matcher.end() + start);
            _findBtn.requestFocusInWindow();
        }
        else {
            if (_wrapBox.isSelected() && _allBtn.isSelected()) {
                matcher = sDoc.getMatcher(_pattern);
                if (matcher.find()) {
                    found = true;
                    _comp.select(matcher.start(), matcher.end());
                    _findBtn.requestFocusInWindow();
                }
            }
        }

        return found;
    }

    private int doReplace(boolean replaceAll) {
        SyntaxDocument sDoc = SyntaxUtils.getSyntaxDocument(_comp);
        if (_pattern == null || sDoc == null)
            return 0;

        String replacement = (String)_replaceBox.getSelectedItem();
        if (replacement == null)
            replacement = "";
        insertIntoCombo(_replaceBox, replacement);

        int start = _comp.getSelectionStart();
        int count = 0;
        try {
            // we can't replace as we go since we need to replace in revert order (because of the replacement), but the Matcher can't search from the end
            List<Integer> starts = new ArrayList<>(), ends = new ArrayList<>();
            Matcher matcher = sDoc.getMatcher(_pattern, start);
            while (matcher.find() && (_allBtn.isSelected() || matcher.end() + start < _comp.getSelectionEnd())) {
                starts.add(matcher.start() + start);
                ends.add(matcher.end() + start);
                count++;

                if (!replaceAll)
                    break;
            }

            for (int i = starts.size() - 1; i >= 0; i--)
                sDoc.replace(starts.get(i), ends.get(i) - starts.get(i), replacement, null);
        }
        catch (BadLocationException e) {
            throw new RuntimeException("Unable to replace text", e);
        }

        return count;
    }

    private int doCount() {
        SyntaxDocument sDoc = SyntaxUtils.getSyntaxDocument(_comp);
        if (_pattern == null || sDoc == null)
            return 0;

        // always start the count at the beginning of the document
        int start = 0;

        // check if we need to search within the selected text only...
        if (_selectedBtn.isSelected())
            start = _comp.getSelectionStart();

        int count = 0;
        Matcher matcher = sDoc.getMatcher(_pattern, start);
        while (matcher.find() && (_allBtn.isSelected() || matcher.end() + start < _comp.getSelectionEnd()))
            count++;

        return count;
    }

    private void updatePattern() {
        int flag = 0;

        if (!_regexBox.isSelected())
            flag |= Pattern.LITERAL;

        if (!_caseBox.isSelected())
            flag |= Pattern.CASE_INSENSITIVE;

        String regex = ((JTextField)_searchBox.getEditor().getEditorComponent()).getText();
        if (regex != null && regex.length() > 0) {
            try {
                _pattern = Pattern.compile(regex, flag);
                insertIntoCombo(_searchBox, regex);
                ((JTextField)_searchBox.getEditor().getEditorComponent()).setText(regex);
            }
            catch (PatternSyntaxException e) {
                _pattern = null;
            }
        }
        else
            _pattern = null;

        String replaceWith = ((JTextField)_replaceBox.getEditor().getEditorComponent()).getText();
        if (replaceWith != null && replaceWith.length() > 0) {
            insertIntoCombo(_replaceBox, replaceWith);
            ((JTextField)_replaceBox.getEditor().getEditorComponent()).setText(replaceWith);
        }

        SyntaxUtils.removeMarkers(_comp, _marker);
    }

    @SuppressWarnings("unchecked")
    private void insertIntoCombo(JComboBox combo, Object item) {
        MutableComboBoxModel model = (MutableComboBoxModel)combo.getModel();
        if (model.getSize() == 0) {
            model.insertElementAt(item, 0);
            return;
        }

        Object o = model.getElementAt(0);
        if (o.equals(item)) {
            return;
        }
        model.removeElement(item);
        model.insertElementAt(item, 0);
        combo.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("find".equals(e.getActionCommand()))
            performFind();
        if ("replace-find".equals(e.getActionCommand()))
            performReplaceFind();
        if ("replace".equals(e.getActionCommand()))
            performReplace();
        if ("replace-all".equals(e.getActionCommand()))
            performReplaceAll();
        if ("count".equals(e.getActionCommand()))
            performCount();
        else if ("close".equals(e.getActionCommand()))
            performClose();
    }

    @Override
    public boolean handleShortcut(int key) {
        boolean handled = false;

        if (key == KeyEvent.VK_ESCAPE) {
            performClose();
            handled = true;
        }

        return handled;
    }

    @Override
    public String getWindowId() {
        return this.getName();
    }
}
