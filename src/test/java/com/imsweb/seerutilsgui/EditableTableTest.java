/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.layout.LayoutFactory;
import com.imsweb.layout.record.fixed.FixedColumnsField;
import com.imsweb.layout.record.fixed.naaccr.NaaccrLayout;
import com.imsweb.seerutilsgui.table.SeerCellListener;
import com.imsweb.seerutilsgui.table.SeerColumn;
import com.imsweb.seerutilsgui.table.SeerTable;
import com.imsweb.seerutilsgui.table.SeerTableStringEditor;
import com.imsweb.seerutilsgui.table.SeerTableStringRenderer;

// this is a test
public class EditableTableTest {

    private static final int _COL_IDX_FIELD = 0;
    private static final int _COL_IDX_START = 1;
    private static final int _COL_IDX_END = 2;
    private static final int _COL_IDX_LENGTH = 3;
    private static final int _COL_IDX_PROP = 4;
    private static final int _COL_IDX_NAACCR_NUM = 5;
    private static final int _COL_IDX_SHORT_LBL = 6;
    private static final int _COL_IDX_LONG_LBL = 7;

    private static final Color _COLOR_GAP = new Color(225, 225, 225);

    private static final String _GAP_LABEL = "<gap>";

    private static NaaccrLayout _LAYOUT;
    private static int _LINE_LENGTH;

    private static SeerTable _TABLE;

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        _LAYOUT = (NaaccrLayout)LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13);
        _LINE_LENGTH = _LAYOUT.getLayoutLineLength();

        List<FixedColumnsField> originalFields = new ArrayList<>();
        for (FixedColumnsField f : _LAYOUT.getAllFields()) {
            if (f.getSubFields() == null)
                originalFields.add(f);
            else {
                boolean subFieldsHaveNaaccrItemNumber = true;
                for (FixedColumnsField ff : f.getSubFields())
                    subFieldsHaveNaaccrItemNumber &= ff.getNaaccrItemNum() != null;

                // if subfields have a NAACCR item number, add them instead of the parent, otherwise use the parent
                if (subFieldsHaveNaaccrItemNumber) {
                    for (FixedColumnsField ff : f.getSubFields())
                        originalFields.add(ff);
                }
                else
                    originalFields.add(f);
            }
        }

        //originalFields.get(1).setEnd(500);

        List<EditableField> fields = addGaps(originalFields);

        final JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 900));

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        List<SeerColumn> columns = new ArrayList<>();
        columns.add(new SeerColumn("Field").setContentType(EditableField.class).setVisible(false));
        columns.add(new SeerColumn("Start").setWidth(SeerColumn.SeerColumnWidthType.FIXED).setFixedSize(60).setEditable(true));
        columns.add(new SeerColumn("End").setWidth(SeerColumn.SeerColumnWidthType.FIXED).setFixedSize(60).setEditable(true));
        columns.add(new SeerColumn("Length").setWidth(SeerColumn.SeerColumnWidthType.FIXED).setFixedSize(60).setEditable(true));
        columns.add(new SeerColumn("Property Name").setEditable(true));
        columns.add(new SeerColumn("NAACCR #").setWidth(SeerColumn.SeerColumnWidthType.FIXED).setFixedSize(80).setEditable(true));
        columns.add(new SeerColumn("Short Label").setEditable(true));
        columns.add(new SeerColumn("Long Label").setEditable(true));

        Vector<Vector<Object>> data = new Vector<>();
        for (EditableField field : fields) {
            Vector<Object> row = new Vector<>();
            row.add(field);
            row.add(field.getStart().toString());
            row.add(field.getEnd().toString());
            row.add(field.getLength().toString());
            row.add(field.getName());
            row.add(field.getNaaccrItemNum() == null ? null : field.getNaaccrItemNum().toString());
            row.add(field.getShortLabel());
            row.add(field.getLongLabel());
            data.add(row);
        }

        SeerCellListener listener = e -> SwingUtilities.invokeLater(EditableTableTest::validateTableContent);

        _TABLE = new SeerTable(columns, data, true, false, false, listener) {
            @Override
            public boolean isCellEditable(int row, int column) {
                EditableField field = (EditableField)fetchValue(row, _COL_IDX_FIELD);
                if (_GAP_LABEL.equals(field.getName()))
                    return false;
                return super.isCellEditable(row, column);
            }
        };

        SeerTableStringEditor editor = new SeerTableStringEditor(listener) {

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
                _field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            stopCellEditing();
                            e.consume();
                        }
                    }
                });
                return comp;
            }

            @Override
            public boolean stopCellEditing() {

                EditableField currentField = (EditableField)_TABLE.fetchValue(_row, _COL_IDX_FIELD);
                // get previous field, ignoring any gaps
                EditableField previousField = null;
                for (int r = _row - 1; r >= 0 && previousField == null; r--)
                    if (!_GAP_LABEL.equals(((EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD)).getName()))
                        previousField = (EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD);
                // get next field, ignoring any gaps
                EditableField nextField = null;
                for (int r = _row + 1; r < _TABLE.getRowCount() && nextField == null; r++)
                    if (!_GAP_LABEL.equals(((EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD)).getName()))
                        nextField = (EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD);

                String errorMsg = validateField(_TABLE.getEditingColumn() + 1, getCellEditorValue(), previousField, currentField, nextField, _LINE_LENGTH);
                if (errorMsg != null) {
                    JOptionPane.showMessageDialog(frame, errorMsg, "Invalid value", JOptionPane.ERROR_MESSAGE);
                    SwingUtilities.invokeLater(() -> {
                        _field.requestFocusInWindow();
                        _field.selectAll();
                    });
                    return false;
                }

                int col = _col + 1;
                int selectedRow = _TABLE.getSelectedRow();
                if (col == _COL_IDX_END || col == _COL_IDX_LENGTH) {

                    // update other field (end updates length and length updates end)
                    Integer start = Integer.valueOf((String)_TABLE.fetchValue(_row, _COL_IDX_START));
                    Integer end;
                    if (col == _COL_IDX_END) {
                        end = Integer.valueOf((String)getCellEditorValue());
                        _TABLE.updateValue(String.valueOf(end - start + 1), _row, _COL_IDX_LENGTH);
                    }
                    else {
                        end = start + Integer.valueOf((String)getCellEditorValue()) - 1;
                        _TABLE.updateValue(String.valueOf(end), _row, _COL_IDX_END);
                    }

                    // do we need a gap after the field?
                    if (nextField == null || nextField.getStart() != null) {
                        int gapStart = end + 1;
                        int gapEnd = nextField == null ? _LINE_LENGTH : nextField.getStart() - 1;
                        int gapLength = gapEnd - gapStart + 1;

                        // tricky part, update the gap after the field (might have to create one, delete existing one or just update it)
                        EditableField gap = _row == _TABLE.getRowCount() - 1 ? null : (EditableField)_TABLE.fetchValue(_row + 1, _COL_IDX_FIELD);
                        if (gap != null && !_GAP_LABEL.equals(gap.getName()))
                            gap = null;
                        if (gap != null) {
                            if (gapLength == 0)
                                _TABLE.removeRow(_row + 1);
                            else {
                                _TABLE.updateValue(String.valueOf(gapStart), _row + 1, _COL_IDX_START);
                                _TABLE.updateValue(String.valueOf(gapEnd), _row + 1, _COL_IDX_END);
                                _TABLE.updateValue(String.valueOf(gapLength), _row + 1, _COL_IDX_LENGTH);
                            }
                        }
                        else if (gapLength > 0) {
                            FixedColumnsField f = new FixedColumnsField();
                            f.setStart(gapStart);
                            f.setEnd(gapEnd);
                            f.setName(_GAP_LABEL);
                            EditableField ef = new EditableField(f);
                            Vector<Object> row = new Vector<>();
                            row.add(ef);
                            row.add(ef.getStart() == null ? null : ef.getStart().toString());
                            row.add(ef.getEnd() == null ? null : ef.getEnd().toString());
                            row.add(ef.getLength() == null ? null : ef.getLength().toString());
                            row.add(ef.getName());
                            row.add(ef.getNaaccrItemNum() == null ? null : ef.getNaaccrItemNum().toString());
                            row.add(ef.getShortLabel());
                            row.add(ef.getLongLabel());
                            _TABLE.addRowAfter(row, _row + 1);
                        }
                    }
                }
                if (selectedRow > -1)
                    _TABLE.setRowSelectionInterval(selectedRow, selectedRow);

                return super.stopCellEditing();
            }
        };

        SeerTableStringRenderer renderer = new SeerTableStringRenderer() {
            @Override
            public synchronized Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JComponent comp = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                EditableField field = (EditableField)((SeerTable)table).fetchValue(row, _COL_IDX_FIELD);
                if (_GAP_LABEL.equals(field.getName())) {
                    if (!isSelected)
                        comp.setBackground(_COLOR_GAP);
                    JLabel lbl = null;
                    if (comp.getComponents()[0] instanceof JPanel) {
                        JComponent pnl = (JComponent)comp.getComponents()[0];
                        if (pnl.getComponents()[0] instanceof JLabel)
                            lbl = (JLabel)pnl.getComponents()[0];
                    }
                    if (lbl != null)
                        lbl.setForeground(Color.GRAY);
                }
                else if (field.getInvalidColIndexes().contains(column + 1))
                    comp.setBorder(BorderFactory.createLineBorder(Color.RED));

                return comp;
            }
        };

        _TABLE.setAlternateRowColors(false);
        _TABLE.setDefaultRenderer(String.class, renderer);
        _TABLE.setDefaultEditor(String.class, editor);

        final ActionListener tableListener = e -> {
            String com = e.getActionCommand();
            if ("table-menu-split".equals(com)) {
                int selectedRow = _TABLE.getSelectedRow();
                if (selectedRow == -1)
                    return;

                EditableField field = (EditableField)_TABLE.fetchValue(selectedRow, _COL_IDX_FIELD);
                if (!field.isValid())
                    return;

                int numFields = 10; // TODO FPD
                for (int i = 0; i < numFields; i++) {
                    FixedColumnsField f = new FixedColumnsField();
                    if (i == 0)
                        f.setStart(field.getStart());
                    if (i == numFields - 1)
                        f.setEnd(field.getEnd());

                    EditableField ef = new EditableField(f);

                    Vector<Object> row = new Vector<>();
                    row.add(ef);
                    row.add(ef.getStart() == null ? null : ef.getStart().toString());
                    row.add(ef.getEnd() == null ? null : ef.getEnd().toString());
                    row.add(ef.getLength() == null ? null : ef.getLength().toString());
                    row.add(ef.getName());
                    row.add(ef.getNaaccrItemNum() == null ? null : field.getNaaccrItemNum().toString());
                    row.add(ef.getShortLabel());
                    row.add(ef.getLongLabel());

                    _TABLE.addRowAfter(row, selectedRow++);
                }

                validateTableContent();
            }
            else if ("table-menu-replace-field-by-gap".equals(com)) {
                int selectedRow = _TABLE.getSelectedRow();
                if (selectedRow == -1)
                    return;

                EditableField field = (EditableField)_TABLE.fetchValue(selectedRow, _COL_IDX_FIELD);
                if (!field.isValid())
                    return;
                if (_GAP_LABEL.equals(field.getName()))
                    return;

                FixedColumnsField f = new FixedColumnsField();
                f.setStart(field.getStart());
                f.setEnd(field.getEnd());
                f.setName(_GAP_LABEL);
                EditableField ef = new EditableField(f);
                _TABLE.updateValue(ef, selectedRow, _COL_IDX_FIELD);
                _TABLE.updateValue(ef.getStart().toString(), selectedRow, _COL_IDX_START);
                _TABLE.updateValue(ef.getEnd().toString(), selectedRow, _COL_IDX_END);
                _TABLE.updateValue(ef.getLength().toString(), selectedRow, _COL_IDX_LENGTH);
                _TABLE.updateValue(ef.getName(), selectedRow, _COL_IDX_PROP);
                _TABLE.updateValue(null, selectedRow, _COL_IDX_NAACCR_NUM);
                _TABLE.updateValue(null, selectedRow, _COL_IDX_SHORT_LBL);
                _TABLE.updateValue(null, selectedRow, _COL_IDX_LONG_LBL);

                // TODO if there is a gap before and/or after, merge it...

                validateTableContent();
            }
            else if ("table-menu-add-field-gap-beginning".equals(com)) {
                int selectedRow = _TABLE.getSelectedRow();
                if (selectedRow == -1)
                    return;

                EditableField field = (EditableField)_TABLE.fetchValue(selectedRow, _COL_IDX_FIELD);
                if (!field.isValid())
                    return;
                if (!_GAP_LABEL.equals(field.getName()))
                    return;

                FixedColumnsField f = new FixedColumnsField();
                f.setStart(field.getStart());
                f.setEnd(field.getStart());
                f.setName(null);
                EditableField ef = new EditableField(f);

                Vector<Object> row = new Vector<>();
                row.add(ef);
                row.add(ef.getStart() == null ? null : ef.getStart().toString());
                row.add(ef.getEnd() == null ? null : ef.getEnd().toString());
                row.add(ef.getLength() == null ? null : ef.getLength().toString());
                row.add(ef.getName());
                row.add(ef.getNaaccrItemNum() == null ? null : field.getNaaccrItemNum().toString());
                row.add(ef.getShortLabel());
                row.add(ef.getLongLabel());

                _TABLE.addRowAfter(row, selectedRow);

                // TODO FPD if next gap length become 0 then gap needs to be removed and not updated...
                // TODO FPD I can't update the table like this; the field in column 0 is not in synch anymore...
                _TABLE.updateValue(Integer.valueOf(field.getStart() + 1).toString(), selectedRow + 1, _COL_IDX_START);
                _TABLE.updateValue(Integer.valueOf(ef.getLength() - 1).toString(), selectedRow + 1, _COL_IDX_LENGTH);

                validateTableContent();

                _TABLE.changeSelection(selectedRow, selectedRow, true, true);
                _TABLE.editCellAt(selectedRow, _COL_IDX_PROP - 1);
            }
        };

        _TABLE.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popup = new JPopupMenu("Table Popup");
                    popup.setBorder(new BevelBorder(BevelBorder.RAISED));
                    //int numRows = _TABLE.getSelectedRowCount();
                    /**
                     // copy cell (must be a single row and column selected)
                     JMenuItem copyCellItem = SeerGuiUtils.createMenuItem("Copy Cell to Clipboard", "table-menu-copy-cell", SeerabsTable.this);
                     if (numRows != 1 || _TABLE.getSelectedColumn() == -1)
                     copyCellItem.setEnabled(false);
                     popup.add(copyCellItem);
                     // copy row (must be a single row selected)
                     JMenuItem copyRowItem = SeerGuiUtils.createMenuItem("Copy Row to Clipboard", "table-menu-copy-row", SeerabsTable.this);
                     if (numRows != 1)
                     copyRowItem.setEnabled(false);
                     popup.add(copyRowItem);
                     // copy table (there must be at least one row in the table)
                     JMenuItem copyTableItem = SeerGuiUtils.createMenuItem("Copy Table to Clipboard", "table-menu-copy-table", SeerabsTable.this);
                     if (_TABLE.getRowCount() <= 0)
                     copyTableItem.setEnabled(false);
                     popup.add(copyTableItem);
                     */

                    JMenuItem splitFieldItem = SeerGuiUtils.createMenuItem("Split into several fields", "table-menu-split", tableListener);
                    popup.add(splitFieldItem);
                    popup.addSeparator();
                    JMenuItem replaceFieldByGapItem = SeerGuiUtils.createMenuItem("Replace Field by Gap", "table-menu-replace-field-by-gap", tableListener);
                    popup.add(replaceFieldByGapItem);
                    popup.addSeparator();
                    JMenuItem addFieldInGapBeginningItem = SeerGuiUtils.createMenuItem("Add Field at Beginning of Gap", "table-menu-add-field-gap-beginning", tableListener);
                    popup.add(addFieldInGapBeginningItem);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        contentPnl.add(new JScrollPane(_TABLE), BorderLayout.CENTER);

        JPanel southPnl = SeerGuiUtils.createPanel();
        southPnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        southPnl.add(SeerGuiUtils.createButton("TEST", "test", "test", e -> {
            Vector<Object> row = new Vector<>();
            row.add(new EditableField());
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            _TABLE.addRowBefore(row, 1);
            _TABLE.addRowAfter(row, 2);
            validateTableContent();
        }), BorderLayout.CENTER);
        contentPnl.add(southPnl, BorderLayout.SOUTH);

        SeerGuiUtils.showAndPosition(frame, null);

        SwingUtilities.invokeLater(EditableTableTest::validateTableContent);
    }

    public static class EditableField extends FixedColumnsField {

        private Integer _length;

        private Set<Integer> _invalidColIndexes;

        public EditableField() {
            super();

            _invalidColIndexes = new HashSet<>();
        }

        public EditableField(FixedColumnsField f) {
            this();

            _name = f.getName();
            _start = f.getStart();
            _end = f.getEnd();
            _naaccrItemNum = f.getNaaccrItemNum();
            _shortLabel = f.getShortLabel();
            _longLabel = f.getLongLabel();

            _length = _start == null || _end == null ? null : _end - _start + 1;
        }

        public Integer getLength() {
            return _length;
        }

        public void setLength(Integer length) {
            _length = length;
        }

        public Set<Integer> getInvalidColIndexes() {
            return _invalidColIndexes;
        }

        public boolean isValid() {
            return _invalidColIndexes.isEmpty();
        }
    }

    public static String validateField(int fieldIdx, Object value, EditableField previousField, EditableField currentField, EditableField nextField, int lineLength) {
        String errorMsg = null;

        switch (fieldIdx) {
            case _COL_IDX_FIELD:
                break; // no validation for now
            case _COL_IDX_START:
                // value is required
                if (value == null || value.toString().trim().isEmpty())
                    errorMsg = "Start value is required.";
                // if value is a String, it needs to be all digits
                if (errorMsg == null && value instanceof String && !NumberUtils.isDigits((String)value))
                    errorMsg = "Start value '" + value + "' must be all digits.";
                Integer start = errorMsg != null ? null : value instanceof String ? Integer.valueOf((String)value) : (Integer)value;
                // value must be between 1 and line length
                if (errorMsg == null && (start <= 0 || start >= lineLength))
                    errorMsg = "Start value '" + value + "' must be between 1 and the defined line length '" + lineLength + "'.";
                // value must be smaller than end
                if (errorMsg == null && currentField.getEnd() != null && start > currentField.getEnd())
                    errorMsg = "Start value '" + value + "'  must be smaller or equal to end value '" + currentField.getEnd() + "'.";
                // value must be after end of previous field
                if (errorMsg == null && previousField != null && previousField.getEnd() != null && start <= previousField.getEnd())
                    errorMsg = "Start value '" + value + "' must be greater than the end value of the previous field '" + previousField.getEnd() + "'.";
                break;
            case _COL_IDX_END:
                // value is required
                if (value == null || value.toString().trim().isEmpty())
                    errorMsg = "End value is required.";
                // if value is a String, it needs to be all digits
                if (errorMsg == null && value instanceof String && !NumberUtils.isDigits((String)value))
                    errorMsg = "End value '" + value + "' must be all digits.";
                Integer end = errorMsg != null ? null : value instanceof String ? Integer.valueOf((String)value) : (Integer)value;
                // value must be between 1 and line length
                if (errorMsg == null && (end <= 0 || end >= lineLength))
                    errorMsg = "End value '" + value + "' must be between 1 and the defined line length '" + lineLength + "'.";
                // value must be greater or equal than start
                if (errorMsg == null && currentField.getStart() != null && end < currentField.getStart())
                    errorMsg = "End value '" + value + "'  must be greater or equal to start value '" + currentField.getStart() + "'.";
                // value must be before start of next field
                if (errorMsg == null && nextField != null && nextField.getStart() != null && end >= nextField.getStart())
                    errorMsg = "End value '" + value + "' must be smaller than the start value of the next field '" + nextField.getStart() + "'.";
                break;
            case _COL_IDX_LENGTH:
                // value is required
                if (value == null || value.toString().trim().isEmpty())
                    errorMsg = "Length is required.";
                // if value is a String, it needs to be all digits
                if (errorMsg == null && value instanceof String && !NumberUtils.isDigits((String)value))
                    errorMsg = "Length value '" + value + "' must be all digits.";
                Integer length = errorMsg != null ? null : value instanceof String ? Integer.valueOf((String)value) : (Integer)value;
                // value must be between 1 and line length
                if (errorMsg == null && (length <= 0 || length > lineLength))
                    errorMsg = "Length value '" + value + "' must be between 1 and the defined line length '" + lineLength + "'.";
                // length cannot make the field go over the next one
                if (errorMsg == null && currentField.getStart() != null) {
                    if (nextField != null) {
                        if (nextField.getStart() != null && currentField.getStart() + length - 1 >= nextField.getStart())
                            errorMsg = "Length value '" + value + "' is too big, there is space only for  " + (nextField.getStart() - currentField.getStart()) + " characters.";
                    }
                    else {
                        if (currentField.getStart() + length - 1 >= lineLength)
                            errorMsg = "Length value '" + value + "' is too big, there is space only for  " + (lineLength - currentField.getStart()) + " characters.";
                    }
                }
                break;
            case _COL_IDX_PROP:
                // value is required
                if (value == null || value.toString().trim().isEmpty())
                    errorMsg = "Property name is required.";
                String str = (String)value;
                // value must start with a lower-case
                if (errorMsg == null && !str.matches("[a-z].+"))
                    errorMsg = "Property name must start with a lower-case letter.";
                if (errorMsg == null && str.contains(" "))
                    errorMsg = "Property name cannot contain any space.";
                if (errorMsg == null && !str.matches("[a-zA-Z0-9]+"))
                    errorMsg = "Property name must only contain alpha-numeric characters.";
                break;
            case _COL_IDX_NAACCR_NUM:
                // if value is a String, it needs to be all digits
                if (value != null && !value.toString().isEmpty() && value instanceof String && !NumberUtils.isDigits((String)value))
                    errorMsg = "NAACCR Item Number value '" + value + "' must be all digits.";
                break;
            case _COL_IDX_SHORT_LBL:
                break; // no validation for now
            case _COL_IDX_LONG_LBL:
                break; // no validation for now
            default:
                throw new RuntimeException("Unsupported column index: " + fieldIdx);
        }

        return errorMsg;
    }

    private static List<EditableField> addGaps(List<FixedColumnsField> fields) {
        List<EditableField> result = new ArrayList<>();

        int currentPos = 1;
        for (FixedColumnsField field : fields) {
            if (currentPos < field.getStart() - 1) {
                FixedColumnsField f = new FixedColumnsField();
                f.setStart(currentPos + 1);
                f.setEnd(field.getStart() - 1);
                f.setName(_GAP_LABEL);
                result.add(new EditableField(f));
            }
            result.add(new EditableField(field));
            currentPos = field.getEnd();
        }
        if (currentPos < _LINE_LENGTH) {
            FixedColumnsField f = new FixedColumnsField();
            f.setStart(currentPos + 1);
            f.setEnd(_LINE_LENGTH);
            f.setName(_GAP_LABEL);
            result.add(new EditableField(f));
        }

        return result;
    }

    private static void validateTableContent() {
        for (int row = 0; row < _TABLE.getRowCount(); row++) {
            EditableField field = (EditableField)_TABLE.fetchValue(row, _COL_IDX_FIELD);
            field.getInvalidColIndexes().clear();
            if (!_GAP_LABEL.equals(field.getName())) {
                // get previous field, ignoring any gaps
                EditableField previousField = null;
                for (int r = row - 1; r >= 0 && previousField == null; r--)
                    if (!_GAP_LABEL.equals(((EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD)).getName()))
                        previousField = (EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD);
                // get next field, ignoring any gaps
                EditableField nextField = null;
                for (int r = row + 1; r < _TABLE.getRowCount() && nextField == null; r++)
                    if (!_GAP_LABEL.equals(((EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD)).getName()))
                        nextField = (EditableField)_TABLE.fetchValue(r, _COL_IDX_FIELD);

                for (int col = 0; col < _TABLE.getColumnCount(); col++) {
                    if (validateField(col, _TABLE.fetchValue(row, col), previousField, field, nextField, _LINE_LENGTH) != null) {
                        boolean add = true;
                        // do not add a failure on the start if there is already one on the end of previous field
                        //if (col == _COL_IDX_START && previousField != null && previousField.getInvalidColIndexes().contains(_COL_IDX_END))
                        //    add = false;
                        // do not add a failure on the length if there is already one on the end (since both fields are used for the same purpose)
                        if (col == _COL_IDX_LENGTH && field.getInvalidColIndexes().contains(_COL_IDX_END))
                            add = false;
                        if (add)
                            field.getInvalidColIndexes().add(col);
                    }
                }
            }
        }
    }
}
