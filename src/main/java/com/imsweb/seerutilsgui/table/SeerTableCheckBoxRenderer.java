/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.table;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class SeerTableCheckBoxRenderer extends JPanel implements TableCellRenderer {

    /**
     * Constructor
     * <p/>
     * Created on Jun 9, 2008 by depryf
     * @param colInfo column info
     */
    public SeerTableCheckBoxRenderer(List<SeerColumn> colInfo) {
        this.setLayout(new GridBagLayout());
        this.setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            if (hasFocus) {
                this.setBackground(SeerTable.COLOR_TABLE_ROW_SELECTED);
                this.setBorder(SeerTable.TABLE_FOCUSED_CELL_BORDER);
            }
            else {
                this.setBackground(SeerTable.COLOR_TABLE_ROW_SELECTED);
                this.setBorder(SeerTable.TABLE_DEFAULT_CELL_BORDER);
            }
        }
        else {
            this.setBackground(((row % 2) == 0 || !((SeerTable)table).getAlternateRowColors()) ? SeerTable.COLOR_TABLE_ROW_ODD : SeerTable.COLOR_TABLE_ROW_EVEN);
            this.setBorder(SeerTable.TABLE_DEFAULT_CELL_BORDER);
        }

        this.removeAll();
        this.add((SeerTableCheckBox)value);

        return this;
    }

}
