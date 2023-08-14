/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.imsweb.seerutilsgui.SeerMultiSelectComboBox.SeerMultiSelectCheckBoxValue;

/**
 * This class is a special <b>JComboBox</b> that displays its items as checkboxes and allow several of them to be selected.
 */
public class SeerMultiSelectComboBox<T> extends JComboBox<SeerMultiSelectCheckBoxValue<T>> {

    // default text when no checkbox is selected
    public static final String NO_SELECTION_TEXT = "< No Value Selected >";

    // default text when multiple checkboxes are selected
    public static final String MULTIPLE_SELECTION_TEXT = "< Multiple Values Selected >";

    // default foreground color when no checkbox is selected
    public static final Color NO_SELECTION_COLOR = Color.DARK_GRAY;

    // default foreground color when multiple checkboxes are selected
    public static final Color MULTIPLE_SELECTION_COLOR = Color.DARK_GRAY;

    // default foreground color when a single checkbox is selected
    public static final Color ONE_SELECTION_COLOR = Color.BLACK;

    // the text and colors to use for the JTextField
    private String _multiSelectedTxt;
    private String _noSelectedTxt;
    private Color _multiSelectedColor;
    private Color _noSelectedColor;
    private Color _oneSelectedColor;

    // the instances of the checkboxes
    private final List<SeerMultiSelectCheckBoxValue<T>> _checkboxes;

    /**
     * Constructor.
     * @param items list of items to display as checkboxes
     */
    public SeerMultiSelectComboBox(List<T> items) {

        // initialize a bunch of internal variables
        _multiSelectedTxt = MULTIPLE_SELECTION_TEXT;
        _noSelectedTxt = NO_SELECTION_TEXT;
        _multiSelectedColor = MULTIPLE_SELECTION_COLOR;
        _noSelectedColor = NO_SELECTION_COLOR;
        _oneSelectedColor = ONE_SELECTION_COLOR;
        _checkboxes = new ArrayList<>();

        // it's important to keep track of the longest value in the checkboxes to properly set the preferred size...
        int maxWidth = 0;
        for (T item : items) {
            SeerMultiSelectCheckBoxValue<T> checkBoxValue = new SeerMultiSelectCheckBoxValue<>(item);
            _checkboxes.add(checkBoxValue);
            addItem(checkBoxValue);
            maxWidth = Math.max(maxWidth, checkBoxValue.getPreferredSize().width);
        }

        // we will use our own renderer (obviously)
        setRenderer(new CheckBoxRenderer(_checkboxes));

        // also take into account the controls, and the selection text to get the maximum width
        maxWidth = Math.max(maxWidth, SwingUtilities.computeStringWidth(getFontMetrics(getFont()), _multiSelectedTxt) + 25);
        maxWidth = Math.max(maxWidth, SwingUtilities.computeStringWidth(getFontMetrics(getFont()), _noSelectedTxt) + 25);

        // set the preferred width
        Dimension dim = this.getPreferredSize();
        this.setPreferredSize(new Dimension(maxWidth + 5, dim.height));

        // we need an action listener to properly refresh the component when a checkbox is clicked
        addActionListener(this);
    }

    @Override
    public void setMaximumRowCount(int count) {
        super.setMaximumRowCount(count);
        // take the vertical scrollbar into account for the preferred width
        if (count < _checkboxes.size()) {
            Dimension dim = this.getPreferredSize();
            this.setPreferredSize(new Dimension(dim.width + 15, dim.height));
        }
    }

    /**
     * Get the items corresponding to selected checkboxes.
     * @return the items corresponding to selected checkboxes
     */
    public List<T> getSelectedItems() {
        List<T> selectedItems = new ArrayList<>();
        for (SeerMultiSelectCheckBoxValue<T> dto : _checkboxes)
            if (dto.getItem() != null && dto.isSelected())
                selectedItems.add(dto.getItem());
        return selectedItems;
    }

    /**
     * Set the selected checkboxes.
     * @param items items to select
     */
    public void setSelectedItems(List<T> items) {
        for (SeerMultiSelectCheckBoxValue<T> checkBoxDto : _checkboxes)
            checkBoxDto.setSelected(false);
        for (T option : items) {
            for (SeerMultiSelectCheckBoxValue<T> dto : _checkboxes) {
                if (dto.getItem() != null && dto.getItem().equals(option)) {
                    dto.setSelected(true);
                    break;
                }
            }
        }
        refreshComponent();
    }

    /**
     * Use this method to customize the text when multiple checkboses are selected.
     * @param text text to use
     */
    public void setMultipleSelectionText(String text) {
        _multiSelectedTxt = text;
        // re-adjust the preferred size so the full label is always visible...
        Dimension dim = this.getPreferredSize();
        this.setPreferredSize(new Dimension(Math.max(dim.width, SwingUtilities.computeStringWidth(getFontMetrics(getFont()), _multiSelectedTxt) + 25), dim.height));
    }

    /**
     * Use this method to customize the text when no checkbox is selected.
     * @param text text to use
     */
    public void setNoSelectionText(String text) {
        _noSelectedTxt = text;
        // re-adjust the preferred size so the full label is always visible...
        Dimension dim = this.getPreferredSize();
        this.setPreferredSize(new Dimension(Math.max(dim.width, SwingUtilities.computeStringWidth(getFontMetrics(getFont()), _noSelectedTxt) + 25), dim.height));
    }

    /**
     * Use this method to customize the foreground color of the text when several checkboxes are selected.
     * @param color color to use
     */
    public void setMultipleSelectionForeground(Color color) {
        _multiSelectedColor = color;
    }

    /**
     * Use this method to customize the foreground color of the text when exactly one checkboxe is selected.
     * @param color color to use
     */
    public void setOneForeground(Color color) {
        _oneSelectedColor = color;
    }

    /**
     * Use this method to customize the foreground color of the text when no checkboxe us selected.
     * @param color color to use
     */
    public void setNoSelectionForeground(Color color) {
        _noSelectedColor = color;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = getSelectedIndex();
        if (index < 0 || index >= _checkboxes.size())
            return;

        SeerMultiSelectCheckBoxValue<T> cb = _checkboxes.get(index);
        if (cb.getItem() != null) // null means a separator; this should never happen, but better safe than sorry
            cb.setSelected(!cb.isSelected());

        this.repaint();
    }

    @Override
    public void setPopupVisible(boolean flag) {
        // this will prevents the popup from closing after the user clicks one of the checkboxes
    }

    protected void refreshComponent() {
        this.repaint();
    }

    /**
     * With so much customization in the rendering, we obviously need to use our own renderer object! :-)
     */
    @SuppressWarnings({"java:S3740", "rawtypes"}) // some parametrization waring, no idea how to resolve it, doesn't see to cause any real issue...
    private class CheckBoxRenderer implements ListCellRenderer<SeerMultiSelectCheckBoxValue> {

        private final BasicComboBoxRenderer _defaultRenderer = new BasicComboBoxRenderer();
        private final List<SeerMultiSelectCheckBoxValue<T>> _componentList;
        private final JPanel _separator;

        public CheckBoxRenderer(List<SeerMultiSelectCheckBoxValue<T>> checkboxes) {
            _componentList = checkboxes;
            _separator = new JPanel();
            _separator.setOpaque(true);
            _separator.setBackground(Color.WHITE);
            _separator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10), BorderFactory.createLineBorder(Color.GRAY)));
            _separator.setPreferredSize(new Dimension(_separator.getPreferredSize().width, 1));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SeerMultiSelectCheckBoxValue> list, SeerMultiSelectCheckBoxValue value, int index, boolean isSelected, boolean cellHasFocus) {
            Component result = null;

            // regular indexes (the checkboxes)
            if (index >= 0 && index < _componentList.size()) {
                SeerMultiSelectCheckBoxValue<T> checkbox = _componentList.get(index);
                if (checkbox.getItem() != null) {
                    checkbox.setBackground(isSelected ? SeerList.COLOR_LIST_ROW_SELECTED : Color.WHITE);
                    checkbox.setForeground(Color.BLACK);
                    result = checkbox;
                }
                else
                    result = _separator;
            }

            if (index == -1) { // -1 is the combobox text
                String str;
                List<T> selectedItems = getSelectedItems();
                if (selectedItems == null || selectedItems.isEmpty()) {
                    str = _noSelectedTxt;
                    SeerMultiSelectComboBox.this.setForeground(_noSelectedColor);
                }
                else if (selectedItems.size() == 1) {
                    str = selectedItems.toArray()[0].toString();
                    SeerMultiSelectComboBox.this.setForeground(_oneSelectedColor);
                }
                else {
                    str = _multiSelectedTxt;
                    SeerMultiSelectComboBox.this.setForeground(_multiSelectedColor);
                }

                result = _defaultRenderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
            }

            return result;
        }
    }

    /**
     * DTO representing the items as checkboxes
     */
    public static class SeerMultiSelectCheckBoxValue<V> extends JCheckBox implements Serializable {

        private final transient V _item;

        public SeerMultiSelectCheckBoxValue(V item) {
            super(item == null ? null : item.toString());
            _item = item;
        }

        public V getItem() {
            return _item;
        }
    }
}