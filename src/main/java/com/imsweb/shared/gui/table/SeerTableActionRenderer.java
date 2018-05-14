/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class SeerTableActionRenderer extends JPanel implements TableCellRenderer {

    /**
     * Constructor
     * <p/>
     * Created on Jun 9, 2008 by depryf
     */
    public SeerTableActionRenderer() {
        super();

        this.setOpaque(true);
        this.setBorder(BorderFactory.createEmptyBorder(1, SeerTableActionButton.ACTION_BUTTON_GAP, 1, SeerTableActionButton.ACTION_BUTTON_GAP));
        this.setLayout(new GridBagLayout());
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        SeerColumn colInfo = ((SeerTable)table).getColumnInfo().get(table.convertColumnIndexToModel(column));

        this.setToolTipText(colInfo.getTooltip());

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
        if (value != null)
            this.add((SeerTableActionButton)value);
        else
            this.add(Box.createRigidArea(new Dimension(SeerTableActionButton.ACTION_BUTTON_SIZE, SeerTableActionButton.ACTION_BUTTON_SIZE)));

        return this;
    }
}
