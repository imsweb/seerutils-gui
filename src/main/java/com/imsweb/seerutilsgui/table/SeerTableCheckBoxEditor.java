/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.table;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class SeerTableCheckBoxEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private SeerTableCheckBox _box;

    private int _row;
    private int _col;

    private JTable _table;

    private boolean _editedByKey;

    private final transient SeerCellListener _cellListener;

    public SeerTableCheckBoxEditor(SeerCellListener l) {
        _cellListener = l;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e == null) // allow programmatical editing
            return true;

        if (e instanceof MouseEvent) {
            _editedByKey = false;
            return true;
        }

        if (e instanceof KeyEvent) {
            _editedByKey = true;
            return ((KeyEvent)e).getKeyCode() == KeyEvent.VK_ENTER;
        }

        return false;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        _row = table.convertRowIndexToModel(row);
        _col = column;
        _table = table;
        _box = (SeerTableCheckBox)value;

        _box.addActionListener(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SeerTable.COLOR_TABLE_ROW_SELECTED);
        panel.setBorder(SeerTable.TABLE_FOCUSED_CELL_BORDER);
        panel.add(_box);

        // this makes sure that when the user hit ENTER, the action takes place right away...
        if (_editedByKey && _box.isEnabled()) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                _box.setSelected(_box.isSelected() ? Boolean.FALSE : Boolean.TRUE);
                stopCellEditing();
            });
        }

        return panel;
    }

    @Override
    public boolean stopCellEditing() {
        super.stopCellEditing();

        _box.removeActionListener(this);
        if (_cellListener != null && _box.isEnabled())
            _cellListener.actionPerformed(new SeerCellEvent(_table, 1, _box.getActionCommand(), _row, _col));

        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return _box;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stopCellEditing();
    }
}
