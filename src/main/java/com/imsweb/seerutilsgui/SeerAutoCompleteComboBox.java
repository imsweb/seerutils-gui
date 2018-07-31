/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * A special JComboBox that allows auto-completion based on the its content.
 * <p/>
 * Created on Aug 2, 2010 by depryf
 * @author murphyr
 */
public class SeerAutoCompleteComboBox extends JComboBox {

    public enum SeerAutoCompleteComboBoxSearchType {
        STARTS_WITH, CONTAINS, REGEX
    }

    /**
     * The search type
     */
    private SeerAutoCompleteComboBoxSearchType _searchType;

    /**
     * Special model for this component
     */
    private SeerAutoCompleteComboBoxModel _model;

    /**
     * The text component used for auto-completion
     */
    private final JTextComponent _textComponent;

    /**
     * Keep track of the previous pattern
     */
    private String _previousPattern;

    /**
     * Whether the model is being filled-in
     */
    private boolean _modelFilling;

    /**
     * Whether the popup needs to be updated
     */
    private boolean _updatePopup;

    /**
     * Constructor.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param list list of values to display in the combo-box
     */
    public SeerAutoCompleteComboBox(List<String> list) {
        this(list, SeerAutoCompleteComboBoxSearchType.STARTS_WITH);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param list list of values to display in the combo-box
     * @param type the search type
     */
    public SeerAutoCompleteComboBox(List<String> list, SeerAutoCompleteComboBoxSearchType type) {
        this(list, type, 20);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param list list of values to display in the combo-box
     * @param type the search type
     * @param refreshRateMs how often do we need to check whether the popup needs to be refreshed (default is 20ms)
     */
    @SuppressWarnings("unchecked")
    public SeerAutoCompleteComboBox(List<String> list, SeerAutoCompleteComboBoxSearchType type, int refreshRateMs) {

        _searchType = type;

        // using a special model
        _model = new SeerAutoCompleteComboBoxModel(list);
        setModel(_model);

        // auto-completion makes sense only with an editable combo-box!
        setEditable(true);

        // we start with no pattern
        setPattern(null);
        _modelFilling = false;
        _updatePopup = false;

        // also using a special document
        _textComponent = (JTextComponent)getEditor().getEditorComponent();
        _textComponent.setDocument(new AutoCompleteDocument());

        // by default, nothing is selected
        setSelectedItem(null);

        // refresh the popup every 20 ms
        new Timer(refreshRateMs, e -> {
            //noinspection ConstantConditions
            if (_updatePopup && isDisplayable()) {
                setPopupVisible(false);
                if (_model.getSize() > 0)
                    setPopupVisible(true);
                _updatePopup = false;
            }
        }).start();
    }

    /**
     * Returns the (read-only) list of values.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @return list of values
     */
    public List<String> getList() {
        return Collections.unmodifiableList(_model.getList());
    }

    /**
     * Special class to use as the text component document.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @author depryf
     */
    private class AutoCompleteDocument extends PlainDocument {

        private boolean _arrowKeyPressed = false;

        public AutoCompleteDocument() {
            _textComponent.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
                        _arrowKeyPressed = true;
                    else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        //deleting history
                        String text = _textComponent.getText();
                        delete(text);
                        try {
                            clearSelection();
                            updateModel();
                        }
                        catch (BadLocationException e1) {
                            throw new RuntimeException(e1);
                        }
                    }
                }
            });
        }

        public void updateModel() throws BadLocationException {
            String textToMatch = getText(0, getLength());
            setPattern(textToMatch);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            if (_modelFilling)
                return;

            super.remove(offs, len);
            if (_arrowKeyPressed)
                _arrowKeyPressed = false;
            else
                updateModel();

            clearSelection();
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (_modelFilling)
                return;

            // insert the string into the document
            super.insertString(offs, str, a);

            String text = getText(0, getLength());
            if (_arrowKeyPressed) {
                _model.setSelectedItem(text);
                _arrowKeyPressed = false;
            }
            else if (!text.equals(getSelectedItem()))
                updateModel();

            clearSelection();
        }
    }

    public void setText(String text) {
        if (_model._data.contains(text))
            setSelectedItem(text);
        else {
            addToTop(text);
            setSelectedIndex(0);
        }
    }

    public String getText() {
        return getEditor().getItem().toString();
    }

    public void resetPopupContent() {
        setPattern(null);
    }

    private void setPattern(String pattern) {
        if (pattern != null && pattern.trim().isEmpty())
            pattern = null;

        if (_previousPattern == null && pattern == null || pattern != null && pattern.equals(_previousPattern))
            return;

        _previousPattern = pattern;
        _modelFilling = true;
        _model.setPattern(pattern);
        _modelFilling = false;
        if (pattern != null)
            _updatePopup = true;
    }

    private void clearSelection() {
        int i = getText().length();
        _textComponent.setSelectionStart(i);
        _textComponent.setSelectionEnd(i);
    }

    public synchronized void addToTop(String aString) {
        _model.addToTop(aString);
    }

    public synchronized void delete(String aString) {
        _model.delete(aString);
    }

    /**
     * Special class to use as the combo-box model.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @author depryf
     */
    private class SeerAutoCompleteComboBoxModel extends AbstractListModel implements ComboBoxModel {

        private String _selected;
        private static final int _LIMIT = 20;
        private Data _data = new Data();

        class Data {

            private List<String> _list = new ArrayList<>(_LIMIT);
            private List<String> _filtered;

            public void add(String s) {
                _list.add(s);
            }

            public void delete(String s) {
                _list.remove(s);
            }

            public void addToTop(String s) {
                _list.add(0, s);
            }

            public void remove(int index) {
                _list.remove(index);
            }

            public List<String> getList() {
                return _list;
            }

            public List<String> getFiltered() {
                if (_filtered == null)
                    _filtered = _list;
                return _filtered;
            }

            public int size() {
                return _list.size();
            }

            public void setPattern(String pattern) {
                if (pattern == null || pattern.isEmpty()) {
                    _filtered = _list;
                    SeerAutoCompleteComboBox.this.setSelectedItem(_model.getElementAt(0));
                }
                else {
                    _filtered = new ArrayList<>(_LIMIT);
                    String searchPattern = pattern.toLowerCase();

                    switch (_searchType) {
                        case STARTS_WITH:
                            for (String s : _list)
                                if (s.toLowerCase().startsWith(searchPattern))
                                    _filtered.add(s);
                            break;
                        case CONTAINS:
                            for (String s : _list)
                                if (s.toLowerCase().contains(searchPattern))
                                    _filtered.add(s);
                            break;
                        case REGEX:
                            try {
                                Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                                for (String s : _list) {
                                    Matcher m = p.matcher(s);
                                    if (m.find())
                                        _filtered.add(s);
                                }
                            }
                            catch (PatternSyntaxException e) {
                                return;
                            }
                            break;
                        default:
                            throw new RuntimeException("Unsupported search type: " + _searchType);
                    }
                }

                SeerAutoCompleteComboBox.this.setSelectedItem(pattern);
            }

            public boolean contains(String s) {
                if (s == null || s.trim().isEmpty())
                    return true;
                for (String item : _list)
                    if (item.toLowerCase().equals(s.toLowerCase()))
                        return true;
                return false;
            }
        }

        public SeerAutoCompleteComboBoxModel(List<String> list) {
            for (String s : list)
                _data.add(s);
        }

        public void setPattern(String pattern) {

            int size1 = getSize();
            _data.setPattern(pattern);
            int size2 = getSize();

            if (size1 < size2) {
                fireIntervalAdded(this, size1, size2 - 1);
                fireContentsChanged(this, 0, size1 - 1);
            }
            else if (size1 > size2) {
                fireIntervalRemoved(this, size2, size1 - 1);
                fireContentsChanged(this, 0, size2 - 1);
            }
        }

        public void addToTop(String aString) {
            if (aString == null || _data.contains(aString))
                return;
            if (_data.size() == 0)
                _data.add(aString);
            else
                _data.addToTop(aString);

            while (_data.size() > _LIMIT) {
                int index = _data.size() - 1;
                _data.remove(index);
            }

            setPattern(null);
            _model.setSelectedItem(aString);
        }

        public void delete(String aString) {
            _data.delete(aString);
        }

        @Override
        public Object getSelectedItem() {
            return _selected;
        }

        @Override
        public void setSelectedItem(Object anObject) {
            if ((_selected != null && !_selected.equals(anObject)) || _selected == null && anObject != null) {
                _selected = (String)anObject;
                fireContentsChanged(this, -1, -1);
            }
        }

        @Override
        public int getSize() {
            return _data.getFiltered().size();
        }

        @Override
        public Object getElementAt(int index) {
            return _data.getFiltered().get(index);
        }

        public List<String> getList() {
            return _data.getList();
        }
    }
}
