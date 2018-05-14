/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;

/**
 * This class is a special <b>JComboBox</b> that displays its items as checkboxes and allow several of them to be selected.
 */
public class SeerMultiSelectComboBox<T> extends JComboBox {

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
    private String _multSelectedTxt, _noSelectedTxt;
    private Color _multSelectedColor, _noSelectedColor, _oneSelectedColor;

    // whether the controls should be displayed
    private boolean _showControls, _showCloseBtn;

    // the instances of the checkboxes
    private List<SeerMultiSelectCheckBoxDto> _checkboxes;

    // the customized UI to use
    private SeerMultiSelectComboBoxUI _ui;

    /**
     * Constructor.
     * @param items list of items to display as checkboxes
     */
    public SeerMultiSelectComboBox(List<T> items) {
        this(items, false);
    }

    /**
     * Constructor.
     * @param items list of items to display as checkboxes
     * @param showControls if true, the control buttons will be displayed at the bottom of the popup
     */
    public SeerMultiSelectComboBox(List<T> items, boolean showControls) {
        this(items, showControls, false);
    }

    /**
     * Constructor.
     * @param items list of items to display as checkboxes
     * @param showControls if true, the control buttons will be displayed at the bottom of the popup
     * @param showCloseBtn if true, the controls will include a close button; this has no effect if showControls is set to false
     */
    @SuppressWarnings("unchecked")
    public SeerMultiSelectComboBox(List<T> items, boolean showControls, boolean showCloseBtn) {

        // initialize a bunch of internal variables
        _multSelectedTxt = MULTIPLE_SELECTION_TEXT;
        _noSelectedTxt = NO_SELECTION_TEXT;
        _multSelectedColor = MULTIPLE_SELECTION_COLOR;
        _noSelectedColor = NO_SELECTION_COLOR;
        _oneSelectedColor = ONE_SELECTION_COLOR;
        _showControls = showControls;
        _showCloseBtn = showCloseBtn;
        _checkboxes = new ArrayList<>();

        // the customized UI is used to add the controls at the bottom; it would be simple to add support for other 
        // look-and-feel, but all our SEER applications use the Windows L&A, so what's the point. If for some reason 
        // the L&A is not the Windows one, the controls won't be available...
        if (getUI() instanceof WindowsComboBoxUI) {
            _ui = new SeerMultiSelectComboBoxUI();
            setUI(_ui);
        }
        else if (_showControls)
            throw new RuntimeException("Controls are available only with the Windows Look And Feel...");

        // it's important to keep track of the longest value in the checkboxes to properly set the preferred size...
        int maxWidth = 0;
        for (T item : items) {
            SeerMultiSelectCheckBoxDto checkBoxDto = new SeerMultiSelectCheckBoxDto(item);
            _checkboxes.add(checkBoxDto);
            addItem(checkBoxDto);
            maxWidth = Math.max(maxWidth, checkBoxDto.getPreferredSize().width);
        }

        // we will use our own renderer (obviously)
        setRenderer(new CheckBoxRenderer(_checkboxes));

        // also take into account the controls, and the selection text to get the maximum width
        if (_ui != null)
            maxWidth = Math.max(maxWidth, _ui.getPopup().getControlsWidth());
        maxWidth = Math.max(maxWidth, SwingUtilities.computeStringWidth(getFontMetrics(getFont()), _multSelectedTxt) + 25);
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
        for (SeerMultiSelectCheckBoxDto dto : _checkboxes)
            if (dto.getItem() != null && dto.isSelected())
                selectedItems.add(dto.getItem());
        return selectedItems;
    }

    /**
     * Set the selected checkboxes.
     * @param items items to select
     */
    public void setSelectedItems(List<T> items) {
        for (SeerMultiSelectCheckBoxDto checkBoxDto : _checkboxes)
            checkBoxDto.setSelected(false);
        for (T option : items) {
            for (SeerMultiSelectCheckBoxDto dto : _checkboxes) {
                if (dto.getItem() != null && dto.getItem().equals(option)) {
                    dto.setSelected(true);
                    break;
                }
            }
        }
        refreshComponent();
    }

    /**
     * Select all the checkboxes.
     */
    public void selectAll() {
        List<T> l = new ArrayList<>();
        for (SeerMultiSelectCheckBoxDto dto : _checkboxes)
            l.add(dto.getItem());
        setSelectedItems(l);
        refreshComponent();
    }

    /**
     * Clears all the checkboxes
     */
    public void clearAll() {
        setSelectedItems(new ArrayList<>());
        refreshComponent();
    }

    /**
     * Use this method to customize the text when multiple checkboses are selected.
     * @param text text to use
     */
    public void setMultipleSelectionText(String text) {
        _multSelectedTxt = text;
        // re-adjust the preferred size so the full label is always visible...
        Dimension dim = this.getPreferredSize();
        this.setPreferredSize(new Dimension(Math.max(dim.width, SwingUtilities.computeStringWidth(getFontMetrics(getFont()), _multSelectedTxt) + 25), dim.height));
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
        _multSelectedColor = color;
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

        SeerMultiSelectCheckBoxDto cb = _checkboxes.get(index);
        if (cb.getItem() != null) // null means a separator; this should never happen, but better safe than sorry
            cb.setSelected(!cb.isSelected());

        this.repaint();
        if (_ui != null)
            _ui.getPopup().repaint();
    }

    @Override
    public void setPopupVisible(boolean flag) {
        // this will prevents the popup from closing after the user clicks one of the checkboxes
    }

    protected void refreshComponent() {
        this.repaint();
        if (_ui != null)
            _ui.getPopup().repaint();
    }

    /**
     * With so much customizatio in the rendering, we obviously need to use our own renderer object! :-)
     */
    private class CheckBoxRenderer implements ListCellRenderer {

        private final BasicComboBoxRenderer _defaultRenderer = new BasicComboBoxRenderer();
        private final List<SeerMultiSelectCheckBoxDto> _componentList;
        private final JPanel _separator;

        public CheckBoxRenderer(List<SeerMultiSelectCheckBoxDto> checkboxes) {
            _componentList = checkboxes;
            _separator = new JPanel();
            _separator.setOpaque(true);
            _separator.setBackground(Color.WHITE);
            _separator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10), BorderFactory.createLineBorder(Color.GRAY)));
            _separator.setPreferredSize(new Dimension(_separator.getPreferredSize().width, 1));
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component result = null;

            // regular indexes (the check boxes)
            if (index >= 0 && index < _componentList.size()) {
                SeerMultiSelectCheckBoxDto checkbox = _componentList.get(index);
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
                if (selectedItems == null || selectedItems.size() == 0) {
                    str = _noSelectedTxt;
                    SeerMultiSelectComboBox.this.setForeground(_noSelectedColor);
                }
                else if (selectedItems.size() == 1) {
                    str = selectedItems.toArray()[0].toString();
                    SeerMultiSelectComboBox.this.setForeground(_oneSelectedColor);
                }
                else {
                    str = _multSelectedTxt;
                    SeerMultiSelectComboBox.this.setForeground(_multSelectedColor);
                }

                result = _defaultRenderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
            }

            return result;
        }
    }

    /**
     * DTO representing the items as checkboxes
     */
    private class SeerMultiSelectCheckBoxDto extends JCheckBox implements Serializable {

        private transient T _item;

        public SeerMultiSelectCheckBoxDto(T item) {
            super(item == null ? null : item.toString());
            _item = item;
        }

        public T getItem() {
            return _item;
        }
    }

    /**
     * Need to customize the popup (which is implemented as a menu) to add the controls at the bottom...
     */
    private class SeerMultiSelectComboPopup extends BasicComboPopup implements ActionListener {

        private JPanel _extraPnl;

        public SeerMultiSelectComboPopup(JComboBox combo) {
            super(combo);

            if (_showControls) {
                _extraPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
                _extraPnl.setBorder(new SeerOptionalSidesLineBorder(Color.BLACK, true, false, false, false));

                JButton selectAllBtn = new JButton("Select All");
                selectAllBtn.setActionCommand("select-all");
                selectAllBtn.addActionListener(this);
                selectAllBtn.setFocusable(false);
                _extraPnl.add(selectAllBtn);

                JButton clearAllBtn = new JButton("Clear All");
                clearAllBtn.setActionCommand("clear-all");
                clearAllBtn.addActionListener(this);
                clearAllBtn.setFocusable(false);
                _extraPnl.add(clearAllBtn);

                if (_showCloseBtn) {
                    JButton closeBtn = new JButton(" Close ");
                    closeBtn.setActionCommand("close");
                    closeBtn.addActionListener(this);
                    closeBtn.setFocusable(false);
                    _extraPnl.add(closeBtn);
                }

                add(_extraPnl);
            }
        }

        public int getControlsWidth() {
            return _extraPnl == null ? 0 : _extraPnl.getPreferredSize().width;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();
            if ("select-all".equals(action))
                selectAll();
            else if ("clear-all".equals(action))
                clearAll();
            else if ("close".equals(action))
                this.setVisible(false);
        }
    }

    /**
     * This really sucks, but the only way to register our own popup is to have our own UI :-(
     */
    private class SeerMultiSelectComboBoxUI extends WindowsComboBoxUI {

        private SeerMultiSelectComboPopup _popup;

        @Override
        protected ComboPopup createPopup() {
            _popup = new SeerMultiSelectComboPopup(comboBox);
            return _popup;
        }

        public SeerMultiSelectComboPopup getPopup() {
            return _popup;
        }
    }

}