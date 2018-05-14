/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.imsweb.shared.gui.SeerGuiUtils;
import com.imsweb.shared.gui.table.SeerColumn.SeerColumnSortOrderType;
import com.imsweb.shared.gui.table.SeerColumn.SeerColumnWidthType;

/**
 * SeerTable wraps most of the complexity of the JTable...
 * <p/>
 * Created on Aug 13, 2009 by depryf
 * @author depryf
 */
public class SeerTable extends JTable {

    // default size for the rows
    public static final int TABLE_ROW_HEIGHT = 18;

    // colors
    public static final Color COLOR_TABLE_ROW_EVEN = new Color(224, 238, 255);
    public static final Color COLOR_TABLE_ROW_ODD = new Color(255, 255, 255);
    public static final Color COLOR_TABLE_ROW_LBL = new Color(0, 0, 0);
    public static final Color COLOR_TABLE_ROW_SELECTED = new Color(176, 211, 255);
    public static final Color COLOR_TABLE_ROW_SELECTED_LBL = new Color(0, 0, 0);
    public static final Color COLOR_TABLE_CELL_FOCUSED = new Color(136, 189, 255);
    public static final Color COLOR_TABLE_CELL_FOCUSED_LBL = new Color(0, 0, 0);

    // cached borders
    public static final Border TABLE_BORDER_IN = BorderFactory.createLineBorder(SeerGuiUtils.COLOR_COMP_FOCUS_IN);
    public static final Border TABLE_BORDER_OUT = BorderFactory.createLineBorder(SeerGuiUtils.COLOR_COMP_FOCUS_OUT);
    public static final Border TABLE_DEFAULT_CELL_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    public static final Border TABLE_FOCUSED_CELL_BORDER = UIManager.getBorder("Table.focusCellHighlightBorder");

    // action keys for navigation
    public static final String TABLE_ACTION_NEXT_COMP = "table-next-comp";
    public static final String TABLE_ACTION_PREVIOUS_COMP = "table-previous-comp";

    // column information for this table
    protected List<SeerColumn> _colInfo;

    // sorter for this table (left null if no sorter)
    protected TableRowSorter<TableModel> _sorter;

    // which column should be sorted by default (-1 if no sorting)
    protected int _defaultSortColumn = -1;

    // which direction should be used for the default sorted column
    protected SortOrder _defaultSortDirection;

    // whether the alternate colors should be disabled (all rows will be white)
    protected boolean _alternateColors = true;

    // cached table sizes
    protected Map<String, Integer> _cachedLookupSize = new ConcurrentHashMap<>(new HashMap<>());

    // cached search regex
    private static final Pattern _TOKEN_REGEX = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    /**
     * Constructor.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param colInfo list of <code>SeerabsColumn</code> representing the column information
     * @param data table data
     * @param selectable whether or not the rows are selectable
     * @param sortable whether or not the rows are sortable
     * @param addFocusListener if true, a focus listener will be added to set the line borders
     * @param l a <code>SeerabsCellListener<code> (can be null) to listen to action cell events
     */
    public SeerTable(List<SeerColumn> colInfo, Vector<Vector<Object>> data, boolean selectable, boolean sortable, boolean addFocusListener, SeerCellListener l) {

        _colInfo = colInfo;
        SeerTableModel model = new SeerTableModel(colInfo, data);

        this.setModel(model);
        this.setRowHeight(TABLE_ROW_HEIGHT);
        this.setIntercellSpacing(new Dimension(1, 1));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        this.setGridColor(Color.LIGHT_GRAY);
        this.setShowGrid(true);
        this.getTableHeader().setReorderingAllowed(false);
        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        this.setRowSelectionAllowed(selectable);
        this.setPreferredScrollableViewportSize(new Dimension(-1, this.getModel().getRowCount() * this.getRowHeight()));

        this.setDefaultRenderer(String.class, new SeerTableStringRenderer());
        this.setDefaultRenderer(SeerTableActionButton.class, new SeerTableActionRenderer());
        this.setDefaultRenderer(SeerTableCheckBox.class, new SeerTableCheckBoxRenderer(colInfo));

        if (selectable) {
            this.setDefaultEditor(String.class, new SeerTableStringEditor(l));
            this.setDefaultEditor(SeerTableActionButton.class, new SeerTableActionEditor(l));
            this.setDefaultEditor(SeerTableCheckBox.class, new SeerTableCheckBoxEditor(l));
        }

        ((DefaultTableCellRenderer)this.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        this.getTableHeader().setFont(this.getTableHeader().getFont().deriveFont(Font.BOLD));

        // disable default F2 key behavior which is to edit current cell
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "none");
        // disable default ENTER key behavior which is to go to the next row
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        // disable default TAB and SHIFT-TAB behavior which is to navigate between cells
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "none");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK), "none");

        for (int i = _colInfo.size() - 1; i >= 0; i--) {
            SeerColumn dto = _colInfo.get(i);

            this.getColumnModel().getColumn(i).setIdentifier(dto);

            if (!dto.getVisible())
                this.getColumnModel().removeColumn(this.getColumnModel().getColumn(i));

            if (dto.getDefaultSort() != null) {
                _defaultSortColumn = i;
                _defaultSortDirection = dto.getDefaultSort().equals(SeerColumnSortOrderType.ASCENDING) ? SortOrder.ASCENDING : SortOrder.DESCENDING;
            }

            if (dto.getContentType().equals(SeerTableActionButton.class))
                this.setRowHeight(SeerTableActionButton.ACTION_BUTTON_SIZE + SeerTableActionButton.ACTION_BUTTON_GAP);

            if (dto.getContentType().equals(SeerTableActionButton.class) || dto.getContentType().equals(SeerTableCheckBox.class)) {
                this.getColumnModel().getColumn(i).setResizable(false);
                if (i > 0)
                    this.getColumnModel().getColumn(i - 1).setResizable(false);
            }
        }

        if (sortable) {
            _sorter = new TableRowSorter<>(this.getModel());
            this.setRowSorter(_sorter);

            for (int i = _colInfo.size() - 1; i >= 0; i--)
                if (_colInfo.get(i).getContentType().equals(JPanel.class))
                    _sorter.setSortable(i, false);

            if (_defaultSortColumn != -1)
                _sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(_defaultSortColumn, _defaultSortDirection)));
        }

        // add a component listener to automatically re-adjust the columns when the table is resized
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeColumns();
                resizeRows();
            }
        });

        // add a focus listener to show a line border when the focus is gained/lost
        if (addFocusListener) {
            this.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    Container pane = SeerTable.this.getParent().getParent();
                    if (pane instanceof JScrollPane)
                        ((JScrollPane)pane).setBorder(TABLE_BORDER_IN);
                    else
                        SeerTable.this.setBorder(TABLE_BORDER_IN);

                    if (getCellEditor() == null && (getSelectedColumn() == -1 || getSelectedRow() == -1) && getRowCount() > 0)
                        changeSelection(0, 0, false, false);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    Container pane = SeerTable.this.getParent().getParent();
                    if (pane instanceof JScrollPane)
                        ((JScrollPane)pane).setBorder(TABLE_BORDER_OUT);
                    else
                        SeerTable.this.setBorder(TABLE_BORDER_OUT);
                }
            });
        }

        if (selectable)
            this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public boolean getAlternateRowColors() {
        return _alternateColors;
    }

    public void setAlternateRowColors(boolean alternate) {
        _alternateColors = alternate;
    }

    /**
     * Sets the passed component as being the next component after this table in the focus traversal.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param comp <code>JComponent</code>, cannot be null
     */
    public void setNextComponent(final JComponent comp) {
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TABLE_ACTION_NEXT_COMP);
        this.getActionMap().put(TABLE_ACTION_NEXT_COMP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(comp::requestFocusInWindow);
            }
        });
    }

    /**
     * Sets the passed component as being the previous component after this table in the focus traversal.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param comp <code>JComponent</code>, cannot be null
     */
    public void setPreviousComponent(final JComponent comp) {
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK), TABLE_ACTION_PREVIOUS_COMP);
        this.getActionMap().put(TABLE_ACTION_PREVIOUS_COMP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(comp::requestFocusInWindow);
            }
        });
    }

    /**
     * Sets the passed action as the default table action. That action is called when double-clicking a row or hitting the Enter key.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param action <code>AbstractAction</code> to register, cannot be null
     */
    public void setDefaultAction(final AbstractAction action) {

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    boolean hasSelectedColumn = SeerTable.this.getSelectedColumn() != -1;
                    SeerColumn info = hasSelectedColumn ? _colInfo.get(convertColumnIndexToModel(SeerTable.this.getSelectedColumn())) : null;
                    Class<?> clazz = hasSelectedColumn ? info.getContentType() : null;
                    if (!hasSelectedColumn || (!info.getEditable() && !SeerTableActionButton.class.equals(clazz) && !SeerTableCheckBox.class.equals(clazz)))
                        action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "default-action"));
                }
            }
        });

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "default-action");
        this.getActionMap().put("default-action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = SeerTable.this.getSelectedRow();
                int selectedCol = SeerTable.this.getSelectedColumn();
                if (selectedRow == -1 || selectedCol == -1)
                    return;

                int row = convertRowIndexToModel(selectedRow);
                int col = convertColumnIndexToModel(selectedCol);
                SeerColumn info = _colInfo.get(col);
                Class<?> clazz = info.getContentType();
                if (SeerTableActionButton.class.equals(clazz)) {
                    SeerTableActionButton btn = (SeerTableActionButton)SeerTable.this.fetchValue(row, col);
                    if (btn.isEnabled()) {
                        SeerTable.this.editCellAt(SeerTable.this.getSelectedRow(), SeerTable.this.getSelectedColumn());
                        btn.doClick();
                    }
                }
                else if (SeerTableCheckBox.class.equals(clazz)) {
                    SeerTableCheckBox box = (SeerTableCheckBox)SeerTable.this.fetchValue(row, col);
                    if (box.isEnabled()) {
                        SeerTable.this.editCellAt(SeerTable.this.getSelectedRow(), SeerTable.this.getSelectedColumn());
                        box.setSelected(!box.isSelected());
                        SeerTable.this.getCellEditor().stopCellEditing();
                    }
                }
                else if (info.getEditable())
                    SeerTable.this.editCellAt(SeerTable.this.getSelectedRow(), SeerTable.this.getSelectedColumn());
                else
                    action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "default-action"));
            }
        });
    }

    /**
     * Returns the column information for this table.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @return list of <code>SeerabsColumn</code>
     */
    public List<SeerColumn> getColumnInfo() {
        return Collections.unmodifiableList(_colInfo);
    }

    /* (non-Javadoc)
     *
     * Created on Aug 13, 2009 by depryf
     * @see javax.swing.JTable#convertRowIndexToModel(int)
     */
    @Override
    public int convertRowIndexToModel(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount())
            throw new RuntimeException("Bad row index requested for " + getName() + ": " + rowIndex);
        return super.convertRowIndexToModel(rowIndex);
    }

    /* (non-Javadoc)
     *
     * Created on Aug 13, 2009 by depryf
     * @see javax.swing.JTable#convertColumnIndexToModel(int)
     */
    @Override
    public int convertColumnIndexToModel(int colIndex) {
        int numInvisible = 0, numVisible = 0;
        for (SeerColumn info : _colInfo) {
            if (!info.getVisible())
                numInvisible++;
            else {
                if (numVisible == colIndex)
                    return numInvisible + numVisible;
                numVisible++;
            }
        }

        throw new RuntimeException("Bad column index requested for " + getName() + ": " + colIndex);
    }

    /**
     * Resets current sorting.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     */
    public void resetSorting() {
        if (_sorter != null)
            _sorter.setSortKeys(null);
    }

    /**
     * Re-apply default sorting.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     */
    public void reapplyDefaultSorting() {
        if (_sorter != null && _defaultSortColumn != -1)
            _sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(_defaultSortColumn, _defaultSortDirection)));
    }

    /**
     * Sets the row filter according to the provided search text.
     * @param searchText search text (will be tokenized using the splitSearchString() method)
     */
    public void applySearchTextAsRowFilter(String searchText) {
        if (_sorter != null) {
            TableCellRenderer renderer = getDefaultRenderer(String.class);
            if (renderer instanceof SeerTableStringRenderer)
                ((SeerTableStringRenderer)renderer).setHighlighting(searchText);
            final List<String> tokens = splitSearchString(searchText);
            setRowFilter(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<?, ?> entry) {
                    boolean include = true;
                    if (!tokens.isEmpty())
                        include = false;
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (_colInfo.get(i).getVisible()) {
                            for (String token : tokens) {
                                include |= ((String)entry.getValue(i)).toUpperCase().contains(token);
                                if (include)
                                    break;
                            }
                            if (include)
                                break;
                        }
                    }
                    return include;
                }
            });
        }
    }

    /**
     * Sets the passed filter as the row filter.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param filter <code>RowFilter</code>, cannot be null
     */
    public void setRowFilter(RowFilter<Object, Object> filter) {
        if (_sorter != null) {
            _sorter.setRowFilter(filter);
            _sorter.sort();
        }
    }

    /**
     * Resets the current row filter.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     */
    public void resetRowFilter() {
        if (_sorter != null) {
            _sorter.setRowFilter(null);
            TableCellRenderer renderer = getDefaultRenderer(String.class);
            if (renderer instanceof SeerTableStringRenderer)
                ((SeerTableStringRenderer)renderer).resetHighlighting();
            _sorter.sort();
        }
    }

    // **************  Data manupulation methods   ******************

    /**
     * Updates the value for the given row and column.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param value value to set
     * @param row row in model world
     * @param col column in model world
     */
    public void updateValue(Object value, int row, int col) {
        this.getModel().setValueAt(value, row, col);
    }

    /**
     * Gets the value for the given row and column.
     * <p/>
     * NOTE: originally I was overriding the getItem() method but it caused major problem because part
     * of the code was expecting the model row/col and part was expecting the view row/col. I found out
     * it was just easier to leave that method alone...
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param row row in model world
     * @param col column in model world
     * @return the corresponding value
     */
    public Object fetchValue(int row, int col) {
        return this.getModel().getValueAt(row, col);
    }

    /**
     * Adds the passed row.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param row row to add
     */
    public void addRow(Vector<Object> row) {
        ((SeerTableModel)this.getModel()).addRow(row);
    }

    /**
     * Adds the passed row before the requested index.
     * <p/>
     * @param row row to add
     * @param targetRow target index
     */
    public void addRowBefore(Vector<Object> row, int targetRow) {
        ((SeerTableModel)this.getModel()).addRowBefore(row, targetRow);
    }

    /**
     * Adds teh passed row after the requested index.
     * <p/>
     * @param row row to add
     * @param targetRow target index
     */
    public void addRowAfter(Vector<Object> row, int targetRow) {
        ((SeerTableModel)this.getModel()).addRowAfter(row, targetRow);
    }

    /**
     * Adds the passed rows.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param rows rows to add
     */
    public void addRows(Vector<Vector<Object>> rows) {
        ((SeerTableModel)this.getModel()).addRows(rows);
    }

    /**
     * Removes the requested row.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param row row in model world
     */
    public void removeRow(int row) {
        ((SeerTableModel)this.getModel()).removeRow(row);
    }

    /**
     * Remoes all the rows (leaving the data empty).
     * <p/>
     * Created on Aug 13, 2009 by depryf
     */
    public void removeAllRows() {
        ((SeerTableModel)this.getModel()).removeAllRows();
    }

    // **************  Table size methods   ******************

    /**
     * Returns the height this table would take if it wasn't in a JScrollPane.
     * <p/>
     * Created on Nov 6, 2008 by depryf
     * @return height of the table
     */
    public int getPreferredHeightNoScrolling() {
        return getPreferredHeightNoScrolling(0);
    }

    /**
     * Returns the height this table would take if it wasn't in a JScrollPane but was allowed only a certain amount of horizontal space.
     * <p/>
     * Created on Nov 6, 2008 by depryf
     * @param allowedWidth allowed width
     * @return hight of the table
     */
    public int getPreferredHeightNoScrolling(int allowedWidth) {
        int h = 0;

        boolean inScrollPane = this.getParent() instanceof JViewport;

        //System.out.println(" **** Preferred Height");
        for (int col = 0; col < _colInfo.size(); col++) {
            SeerColumn dto = _colInfo.get(col);

            if (dto.getVisible()) {

                int colHeight = inScrollPane ? this.getTableHeader().getPreferredSize().height + 2 : 0;
                //System.out.println(colHeight + " for " + dto.getHeader());

                int colIdx = this.convertColumnIndexToView(col);
                Class<?> clazz = this.getModel().getColumnClass(col);
                for (int row = 0; row < this.getModel().getRowCount(); row++) {
                    Component comp = this.getDefaultRenderer(clazz).getTableCellRendererComponent(this, this.fetchValue(row, col), false, false, row, colIdx);

                    int cellHeight;
                    if (dto.getLongText()) {
                        JPanel panel = (JPanel)comp;
                        JTextArea area = (JTextArea)panel.getComponent(0);
                        area.setSize(allowedWidth, Integer.MAX_VALUE);
                        cellHeight = Math.max(area.getPreferredSize().height, this.getRowHeight());
                    }
                    else
                        cellHeight = Math.max(comp.getPreferredSize().height, this.getRowHeight());
                    //System.out.println(cellHeight + " for " + row + ", " + col);                    
                    colHeight += cellHeight;
                }

                //System.out.println("Total column: " + colHeight);
                h = Math.max(h, colHeight);
            }
        }

        //System.out.println("Returning " + h);
        return h;
    }

    /**
     * Returns the height this table would take if it wasn't in a JScrollPane.
     * <p/>
     * Created on Nov 6, 2008 by depryf
     * @return hight of the table
     */
    public int getPreferredWidthNoScrolling() {
        int w = 0;

        //System.out.println(" **** Preferred Width");
        for (int col = 0; col < _colInfo.size(); col++) {
            SeerColumn dto = _colInfo.get(col);

            if (dto.getVisible()) {
                TableCellRenderer renderer = getColumn(dto).getHeaderRenderer();
                if (renderer == null)
                    renderer = getTableHeader().getDefaultRenderer();
                Component header = renderer.getTableCellRendererComponent(this, dto.getHeader(), false, false, 0, 0);
                int colWidth = header.getPreferredSize().width;
                //System.out.println(colWidth + " for " + dto.getHeader());

                int colIdx = this.convertColumnIndexToView(col);
                Class<?> clazz = this.getModel().getColumnClass(col);
                for (int row = 0; row < this.getModel().getRowCount(); row++) {
                    Component comp = this.getDefaultRenderer(clazz).getTableCellRendererComponent(this, this.fetchValue(row, col), false, false, row, colIdx);

                    int cellWidth = 0;
                    if (dto.getLongText()) {
                        JPanel panel = (JPanel)comp;
                        JTextArea area = (JTextArea)panel.getComponent(0);
                        //System.out.println(" >> " + area.getText());
                        if (area.getText() != null)
                            for (String s : area.getText().split("\n"))
                                cellWidth = Math.max(cellWidth, SwingUtilities.computeStringWidth(area.getFontMetrics(area.getFont()), s));
                        cellWidth += 20;
                    }
                    else
                        cellWidth = comp.getPreferredSize().width + 10;
                    //System.out.println(cellWidth + " for " + row + ", " + col);
                    colWidth = Math.max(colWidth, cellWidth);
                }

                //System.out.println("Total column: " + colWidth);
                w += colWidth;
            }
        }

        //System.out.println("Returning " + w);
        return w;
    }

    /**
     * Resizes the table columns.
     * <p/>
     * Created on Aug 13, 2009 by depryf
     */
    public synchronized void resizeColumns() {
        int tableWidth = getWidth();
        Graphics g = this.getGraphics();
        if (g == null)
            return;

        // gather how much space we have once the specified sizes are gone
        int freeSizes = 0, totalRequiredWith = 0;
        Map<SeerColumn, Integer> dataDrivenSizes = new HashMap<>();
        for (int col = 0; col < _colInfo.size(); col++) {
            SeerColumn dto = _colInfo.get(col);
            if (dto.getVisible()) {
                if (dto.getWidth() == SeerColumnWidthType.FIXED && dto.getFixedSize() != null && dto.getFixedSize() > 0)
                    totalRequiredWith += dto.getFixedSize();
                else if (dto.getWidth() == SeerColumnWidthType.MIN && dto.getContentType() == String.class) {
                    int w = 0;

                    // if  the column has a lookup, set the size according to its lookup
                    if (dto.getLookup() != null) {
                        Integer size = _cachedLookupSize.get(dto.getLookup());
                        if (size == null || size <= 0) {
                            int count = 0, maxSize = 0;
                            for (Map.Entry<String, String> mapping : fetchLookup(dto.getLookup()).entrySet()) {
                                if (mapping.getValue() != null)
                                    maxSize = Math.max(w, SeerTableStringRenderer.getValueWidth(mapping.getValue(), g));

                                if (++count == 100)
                                    break;
                            }

                            size = maxSize;
                            _cachedLookupSize.put(dto.getLookup(), size);
                        }
                        w = size;
                    }
                    else { // go through the 100th first non null values and calculate the max size
                        SeerTableModel model = (SeerTableModel)getModel();
                        for (int i = 0; i < 500 && i < model.getRowCount(); i++) {
                            Object val = model.getValueAt(i, col);
                            if (val != null)
                                w = Math.max(w, SeerTableStringRenderer.getValueWidth(val.toString(), g));
                        }
                    }

                    // never go smaller than the table column header
                    TableCellRenderer headerRenderer = getColumn(dto).getHeaderRenderer();
                    if (headerRenderer == null)
                        headerRenderer = getTableHeader().getDefaultRenderer();
                    Component header = headerRenderer.getTableCellRendererComponent(this, dto.getHeader(), false, false, 0, 0);
                    w = Math.max(w, header.getPreferredSize().width);

                    dataDrivenSizes.put(dto, w);
                    totalRequiredWith += w;
                }
                else
                    freeSizes++;
            }
        }

        int freeSize = freeSizes == 0 ? 0 : (tableWidth - totalRequiredWith) / freeSizes;

        for (SeerColumn dto : _colInfo) {
            if (dto.getVisible()) {
                TableColumn col = getColumn(dto);

                if (dto.getFixedSize() != null)
                    col.setPreferredWidth(dto.getFixedSize());
                else
                    col.setPreferredWidth(dataDrivenSizes.getOrDefault(dto, freeSize));
            }
        }
    }

    /**
     * Resizes the rows of the table
     */
    public synchronized void resizeRows() {

        // only the visible long text columns can change the default size, so let's make sure there is at least one column that will influence the height
        List<Integer> colIndexes = new ArrayList<>();
        for (int colIdx = 0; colIdx < _colInfo.size(); colIdx++) {
            SeerColumn dto = _colInfo.get(colIdx);
            if (dto.getVisible() && dto.getLongText())
                colIndexes.add(colIdx);
        }

        if (!colIndexes.isEmpty()) {
            for (int row = 0; row < getRowCount(); row++) {
                int height = 0;
                for (Integer colIdx : colIndexes) {
                    // it's important to convert the col index because it's the actual index that table needs to know about (since hidden columns are not displayed)
                    int viewColIdx = convertColumnIndexToView(colIdx);
                    TableCellRenderer renderer = getCellRenderer(row, viewColIdx);

                    int h;
                    if (renderer instanceof SeerTableStringRenderer) {
                        Object val = getValueAt(row, viewColIdx);
                        if (val == null)
                            val = _colInfo.get(colIdx).getDefaultValue();
                        h = ((SeerTableStringRenderer)renderer).getTableCellRendererComponentHeight(this, val, false, false, row, viewColIdx);
                    }
                    else
                        h = getRowHeight();
                    height = Math.max(height, h);
                }
                setRowHeight(row, height);
            }
        }
    }

    /**
     * Returns a map representation of the lookup corresponding to the passed ID.
     * <p/>
     * This method is meant to be overridden; this default implementation always returns an empty map.
     * <p/>
     * Created on Apr 8, 2010 by depryf
     * @param id lookup ID
     * @return resulting map
     */
    public Map<String, String> fetchLookup(String id) {
        return Collections.emptyMap();
    }

    // **************  Model for SeerabsTable   ******************

    public class SeerTableModel extends AbstractTableModel {

        /**
         * Columns Info
         */
        private List<SeerColumn> _modelColInfo;

        /**
         * Data
         */
        private Vector<Vector<Object>> _data;

        /**
         * Constructor
         * <p/>
         * Created on Jun 9, 2008 by depryf
         * @param colInfo column information
         * @param data data information
         */
        public SeerTableModel(List<SeerColumn> colInfo, Vector<Vector<Object>> data) {
            _modelColInfo = colInfo;
            _data = data;
        }

        @Override
        public int getColumnCount() {
            return _modelColInfo.size();
        }

        @Override
        public int getRowCount() {
            return _data.size();
        }

        @Override
        public String getColumnName(int col) {
            return _modelColInfo.get(col).getHeader();
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (row < 0 || row >= _data.size())
                throw new RuntimeException("Invalid row index, got " + row + " but table (" + SeerTable.this.getName() + ") has " + _data.size() + " row(s)");
            Vector<Object> objects = _data.get(row);
            if (col < 0 || col >= objects.size())
                throw new RuntimeException("Invalid col index, got " + col + " but table (" + SeerTable.this.getName() + ") has " + objects.size() + " col(s)");
            return objects.get(col);
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (row < 0 || row >= _data.size())
                throw new ArrayIndexOutOfBoundsException("Row index " + row + " out of range 0-" + (_data.size() - 1));
            Vector<Object> objects = _data.get(row);

            if (col < 0 || col >= objects.size())
                throw new ArrayIndexOutOfBoundsException("Col index " + col + " out of range 0-" + (objects.size() - 1));
            objects.set(col, value);
            fireTableCellUpdated(row, col);
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return _modelColInfo.get(col).getContentType();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return _modelColInfo.get(col).getEditable();
        }

        /**
         * Adds the passed row at the end of the data.
         * <p/>
         * Created on Jul 28, 2008 by Fabian
         * @param row row to add
         */
        public void addRow(Vector<Object> row) {
            _data.add(row);
            fireTableDataChanged();
        }

        public void addRowBefore(Vector<Object> row, int targetRow) {
            _data.add(targetRow - 1, row);
            fireTableDataChanged();
        }

        public void addRowAfter(Vector<Object> row, int targetRow) {
            _data.add(targetRow, row);
            fireTableDataChanged();
        }

        /**
         * Removes the passed row
         * <p/>
         * Created on Jul 28, 2008 by Fabian
         * @param row row to remove
         */
        public void removeRow(int row) {
            _data.remove(row);
            fireTableDataChanged();
        }

        /**
         * Adds all the passed rows at the end of the data.
         * <p/>
         * Created on Jul 28, 2008 by Fabian
         * @param rows rows to add
         */
        public void addRows(Vector<Vector<Object>> rows) {
            _data.addAll(rows);
            fireTableDataChanged();
        }

        /**
         * Removes all the rows.
         * <p/>
         * Created on Aug 3, 2008 by Fabian
         */
        public void removeAllRows() {
            _data.clear();
            fireTableDataChanged();
        }
    }

    // **************  Few static helper method to keep the code centralized   ******************

    /**
     * Creates a column info object for a selection column (checkbox).
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @return a <code>SeerabsColumn</code>, never null
     */
    public static SeerColumn createSelectionColumn() {
        SeerColumn dto = new SeerColumn("");

        dto.setContentType(SeerTableCheckBox.class);
        dto.setEditable(true);
        dto.setWidth(SeerColumnWidthType.FIXED);
        dto.setFixedSize(30);

        return dto;
    }

    /**
     * Creates a column info object for an action column (button).
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param tooltip tooltip for the button
     * @return a <code>SeerabsColumn</code>, never null
     */
    public static SeerColumn createActionColumn(String tooltip) {
        SeerColumn dto = new SeerColumn("");

        dto.setContentType(SeerTableActionButton.class);
        dto.setEditable(true);
        dto.setTooltip(tooltip);
        dto.setWidth(SeerColumnWidthType.FIXED);
        dto.setFixedSize(SeerTableActionButton.ACTION_BUTTON_SIZE + (2 * SeerTableActionButton.ACTION_BUTTON_GAP) + 1);

        return dto;
    }

    /**
     * Creates a selection value (for a selection column).
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param command command to use for the returned component
     * @return a new <code>SeerabsTableCheckBox</code>, never null
     */
    public static SeerTableCheckBox createSelectionValue(String command) {
        return new SeerTableCheckBox(command);
    }

    /**
     * Creates an action value (for an action column).
     * <p/>
     * Created on Aug 13, 2009 by depryf
     * @param icon icon to use for the button
     * @param command command to use for the returned component
     * @return a new <code>SeerabsTableActionButton</code>, never null
     */
    public static SeerTableActionButton createActionValue(String icon, String command) {
        return new SeerTableActionButton(icon, command);
    }

    public static List<String> splitSearchString(String searchString) {
        List<String> result = new ArrayList<>();

        if (searchString == null || searchString.trim().isEmpty())
            return result;

        Matcher matcher = _TOKEN_REGEX.matcher(searchString);
        while (matcher.find()) {
            String s;
            if (matcher.group(1) != null)
                s = matcher.group(1);
            else if (matcher.group(2) != null)
                s = matcher.group(2);
            else
                s = matcher.group();

            if (s != null && !s.trim().isEmpty())
                result.add(s.toUpperCase());
        }

        return result;
    }

    public static List<List<Integer>> calculateHighlighting(String text, String searchString) {
        List<List<Integer>> result = new ArrayList<>();

        // make sure there is something to highlight
        if (text == null || text.isEmpty())
            return result;

        // search should not be case-sensitive (and string are not mutable, so we can safely change it here)
        text = text.toUpperCase();

        // split the search string (taking into account quoted values) and iterate over each term;
        for (String searchText : splitSearchString(searchString)) {
            int start = text.indexOf(searchText);
            while (start != -1) {
                int end = start + searchText.length() - 1;

                List<Integer> list = new ArrayList<>(2);
                list.add(start);
                list.add(end);
                result.add(list);

                start = text.indexOf(searchText, end + 1);
            }
        }

        // sort the results by start position (note that there could be some overlapping between the highlighted areas)
        result.sort(Comparator.comparing(l -> l.get(0)));

        return result;
    }
}
