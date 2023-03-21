/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

import com.imsweb.seerutilsgui.SeerGuiUtils;

public class SeerTableStringEditor extends AbstractCellEditor implements TableCellEditor {

    /**
     * Component used as the editor.
     */
    protected JTextArea _field;

    /**
     * The currently edited row and column.
     */
    protected int _row;
    protected int _col;

    /**
     * The parent table.
     */
    protected JTable _table;

    /**
     * An optional cell listener; if not null, it will be notified once the editing is done.
     */
    protected transient SeerCellListener _cellListener;

    /**
     * Constructor
     */
    public SeerTableStringEditor() {
        super();
    }

    /**
     * Constructor
     * @param l cell listener, can be null
     */
    public SeerTableStringEditor(SeerCellListener l) {
        this();
        _cellListener = l;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e == null) // allow programmatical editing
            return true;

        if (e instanceof MouseEvent)
            return ((MouseEvent)e).getClickCount() == 2;

        if (e instanceof KeyEvent)
            return ((KeyEvent)e).getKeyCode() == KeyEvent.VK_ENTER;

        return false;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        _row = table.convertRowIndexToModel(row);
        _col = column;
        _table = table;
        _field = new JTextArea((String)value);
        _field.setFont(SeerGuiUtils.createLabel("").getFont());

        javax.swing.SwingUtilities.invokeLater(() -> {
            _field.requestFocusInWindow();
            _field.selectAll();
        });

        SeerColumn colInfo = ((SeerTable)table).getColumnInfo().get(table.convertColumnIndexToModel(column));
        if (!Boolean.TRUE.equals(colInfo.getLongText())) {
            int i = _table.getRowHeight() - table.getFontMetrics(_field.getFont()).getHeight();
            if (i > 0) {
                _field.getMargin().top = i / 2;
                _field.getMargin().bottom = i / 2;
            }
        }

        JPanel panel = SeerGuiUtils.createPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(_field);

        return panel;
    }

    @Override
    public boolean stopCellEditing() {
        super.stopCellEditing();

        if (_cellListener != null)
            _cellListener.actionPerformed(new SeerCellEvent(_table, 1, "text-action", _row, _col));

        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return _field.getText();
    }
}
