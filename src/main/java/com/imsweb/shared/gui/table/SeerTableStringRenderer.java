/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import com.imsweb.shared.gui.SeerHighlightingEditorKit;

public class SeerTableStringRenderer extends JPanel implements TableCellRenderer {

    // cached component used to render single-line text
    protected final JLabel _lbl;

    // cached component to render multi-line text (long text columns)
    protected final JTextArea _area;

    // cached component used to render highlitable content
    protected final JTextPane _pane;

    // cached component to calculate the preferred hight of a given row (note that I could use the other text area, 
    // but I want to minimize any racing conditions).
    protected final JTextArea _area2;

    // cached font to use for both internal renderers
    private Font _font;

    // if true, we are currently using the single-line renderer, otherwise we are using the multi-line one
    private boolean _usingLbl, _usingBorderLayout;

    // if set to true, highlighting will be enabled allowing filtering the text
    protected boolean _allowHightlighting;

    // cached highlighting editor kits
    private Map<String, SeerHighlightingEditorKit> _highlights;

    // the current search text
    private String _search = null;

    /**
     * Constructor
     * <p/>
     * Created on Jun 9, 2008 by depryf
     */
    public SeerTableStringRenderer() {
        this(false);
    }

    /**
     * Constructor
     * <p/>
     * Created on Jun 9, 2008 by depryf
     * @param allowHighlighting if true, the internal component used for the rendering will be a text editor, allowing chuck of text to be displayed differently
     */
    public SeerTableStringRenderer(boolean allowHighlighting) {
        super();
        this.setOpaque(true);
        this.setBorder(null);
        this.setLayout(new BorderLayout());

        _allowHightlighting = allowHighlighting;

        _lbl = new JLabel();
        _lbl.setOpaque(false);
        _lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        _area = new JTextArea();
        _area.setOpaque(false);
        _area.setLineWrap(true);
        _area.setWrapStyleWord(true);
        _area.setEditable(false);
        _area.setMargin(new Insets(0, 5, 3, 5));
        _area.setFont(_lbl.getFont());

        _area2 = new JTextArea();
        _area2.setOpaque(false);
        _area2.setLineWrap(true);
        _area2.setWrapStyleWord(true);
        _area2.setEditable(false);
        _area2.setMargin(new Insets(0, 5, 3, 5));
        _area2.setFont(_lbl.getFont());

        _pane = new JTextPane();
        _pane.setOpaque(false);
        _pane.setEditable(false);
        _pane.setFont(_lbl.getFont());

        if (allowHighlighting) {
            this.add(_pane, BorderLayout.CENTER);
            _usingBorderLayout = true;
        }
        else {
            this.add(_lbl, BorderLayout.CENTER);
            _usingLbl = true;
        }

        _font = _lbl.getFont();

        _highlights = new HashMap<>();
    }

    @Override
    public synchronized Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        SeerColumn colInfo = ((SeerTable)table).getColumnInfo().get(table.convertColumnIndexToModel(column));

        String text = value == null ? colInfo.getDefaultValue() : value.toString();
        Color foregroundColor = computeForegroundColor(table, value, isSelected, hasFocus, row, column);
        Font font = computeFontStyle(table, value, isSelected, hasFocus, row, column);

        if (_allowHightlighting) {
            if (colInfo.getLongText()) {
                if (!_usingBorderLayout) {
                    this.removeAll();
                    this.setLayout(new BorderLayout());
                    this.add(_pane, BorderLayout.CENTER);
                    _usingBorderLayout = true;
                }
            }
            else {
                if (_usingBorderLayout) {
                    this.removeAll();
                    this.setLayout(new GridBagLayout());
                    this.add(_pane);
                    _usingBorderLayout = false;
                }
            }

            String key = text + foregroundColor.toString();
            SeerHighlightingEditorKit highlighting = _highlights.get(key);
            if (highlighting == null) {
                highlighting = new SeerHighlightingEditorKit(SeerTable.calculateHighlighting(text, _search), Color.RED, foregroundColor, font);
                _highlights.put(key, highlighting);
            }
            _pane.setEditorKit(highlighting);
            if (!colInfo.getLongText() && colInfo.getCenterContent()) {
                int w = (table.getColumnModel().getColumn(column).getWidth() - getValueWidth(text, table.getGraphics())) / 2;
                _pane.setMargin(new Insets(0, 5 + w, 2, 5 + w));
            }
            else
                _pane.setMargin(new Insets(0, 5, 2, 5));
            _pane.setFont(font);
            _pane.setText(text);
        }
        else {
            if (colInfo.getLongText()) {
                if (_usingLbl) {
                    this.removeAll();
                    this.add(_area, BorderLayout.CENTER);
                    _usingLbl = false;
                }

                _area.setFont(font);
                _area.setForeground(foregroundColor);
                _area.setText(text);
            }
            else {
                if (!_usingLbl) {
                    this.removeAll();
                    this.add(_lbl, BorderLayout.CENTER);
                    _usingLbl = true;
                }

                _lbl.setHorizontalAlignment(colInfo.getCenterContent() ? SwingConstants.CENTER : SwingConstants.LEFT);
                _lbl.setFont(font);
                _lbl.setForeground(foregroundColor);
                _lbl.setText(text);
            }
        }

        this.setBackground(computeBackgroundColor(table, value, isSelected, hasFocus, row, column));

        if (isSelected && hasFocus)
            this.setBorder(SeerTable.TABLE_FOCUSED_CELL_BORDER);
        else
            this.setBorder(SeerTable.TABLE_DEFAULT_CELL_BORDER);

        return this;
    }

    protected Color computeBackgroundColor(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color color;

        if (isSelected)
            color = SeerTable.COLOR_TABLE_ROW_SELECTED;
        else {
            if ((row % 2) == 0 || !((SeerTable)table).getAlternateRowColors())
                color = SeerTable.COLOR_TABLE_ROW_ODD;
            else
                color = SeerTable.COLOR_TABLE_ROW_EVEN;
        }

        return color;
    }

    protected Color computeForegroundColor(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color color;

        if (isSelected) {
            if (hasFocus)
                color = SeerTable.COLOR_TABLE_CELL_FOCUSED_LBL;
            else
                color = SeerTable.COLOR_TABLE_ROW_SELECTED_LBL;
        }
        else
            color = SeerTable.COLOR_TABLE_ROW_LBL;

        return color;
    }

    protected Font computeFontStyle(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return _font;
    }

    /**
     * Returns the prefer height of a particular cell.
     * <p/>
     * Created on Apr 1, 2009 by depryf
     * @param table table
     * @param value value
     * @param isSelected whether the cell is selected
     * @param hasFocus whether the cell has focus
     * @param row row index
     * @param column column index
     * @return the prefer height of a particular cell
     */
    public int getTableCellRendererComponentHeight(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        synchronized (_area2) {
            _area2.setText(value == null ? null : value.toString());
            _area2.setFont(computeFontStyle(table, value, isSelected, hasFocus, row, column));
            _area2.setSize(table.getColumnModel().getColumn(column).getWidth(), Integer.MAX_VALUE);
            return Math.max(_area2.getPreferredSize().height, table.getRowHeight());
        }
    }

    public void setHighlighting(String search) {
        _search = search;
        _highlights.clear();
    }

    public void resetHighlighting() {
        _search = null;
        _highlights.clear();
    }

    /**
     * Returns the length of a given string in a table cell.
     * <p/>
     * Created on Oct 30, 2008 by depryf
     * @param val value to evaluate
     * @param g graphics
     * @return the length of a given string in a table cell
     */
    public static int getValueWidth(String val, Graphics g) {
        if (val == null)
            return 0;

        return SwingUtilities.computeStringWidth(g.getFontMetrics(), val) + 14;
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    @Override
    public void repaint(Rectangle r) {
    }

    @Override
    public void repaint() {
    }
}
