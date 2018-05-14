/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.imsweb.shared.gui.table.SeerColumn;
import com.imsweb.shared.gui.table.SeerTable;
import com.imsweb.shared.gui.table.SeerTableStringRenderer;

public class SeerTableTest {

    private static final String _VERSION_TO_HIGHLIGHT = "2.5";
    private static final Color _HIGHLIGHT_COLOR_TEXT = new Color(125, 0, 0);
    private static final Color _HIGHLIGHT_COLOR_ODD = new Color(255, 245, 245);
    private static final Color _HIGHLIGHT_COLOR_EVEN = new Color(255, 225, 225);
    private static final Color _HIGHLIGHT_COLOR_SELECTED = new Color(255, 185, 185);

    public static void main(String[] args) throws Exception {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        final JFrame frame = new JFrame("SEER Table Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 800));

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        // define the columns
        List<SeerColumn> cols = new Vector<>();
        cols.add(new SeerColumn("Hidden").setVisible(false));
        cols.add(new SeerColumn("Version").setWidth(SeerColumn.SeerColumnWidthType.MIN).setCenterContent(true).setDefaultSort(SeerColumn.SeerColumnSortOrderType.DESCENDING));
        cols.add(new SeerColumn("Squish").setWidth(SeerColumn.SeerColumnWidthType.MIN).setCenterContent(true));
        cols.add(new SeerColumn("Ref").setWidth(SeerColumn.SeerColumnWidthType.FIXED).setFixedSize(50).setCenterContent(true));
        cols.add(new SeerColumn("Description").setLongText(Boolean.TRUE));

        // build the data
        Vector<Vector<Object>> data = new Vector<>();
        CSVReader reader = new CSVReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("changelog.csv")));
        for (String[] csvRow : reader.readAll()) {
            String squish = csvRow[0];
            String techSupportSquish = csvRow[1];
            String version = csvRow[2];
            if (version.startsWith("v"))
                version = version.substring(1);
            String desc = csvRow[3];
            if (csvRow.length > 4 && !StringUtils.isBlank(csvRow[4]))
                desc = csvRow[4];

            Vector<Object> row = new Vector<>();
            row.add("context");
            row.add(version);
            row.add(techSupportSquish);
            row.add(squish);
            row.add(desc);
            data.add(row);
        }

        // create the table
        final SeerTable table = new SeerTable(cols, data, true, true, false, null);
        SeerTableStringRenderer renderer = new SeerTableStringRenderer(true) {
            @Override
            protected Color computeBackgroundColor(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (_VERSION_TO_HIGHLIGHT.equals(table.getValueAt(row, 0))) {
                    if (isSelected)
                        return _HIGHLIGHT_COLOR_SELECTED;
                    else {
                        if ((row % 2) == 0 || !((SeerTable)table).getAlternateRowColors())
                            return _HIGHLIGHT_COLOR_ODD;
                        else
                            return _HIGHLIGHT_COLOR_EVEN;
                    }
                }
                return super.computeBackgroundColor(table, value, isSelected, hasFocus, row, column);
            }

            @Override
            protected Color computeForegroundColor(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (_VERSION_TO_HIGHLIGHT.equals(table.getValueAt(row, 0)))
                    return _HIGHLIGHT_COLOR_TEXT;
                return super.computeForegroundColor(table, value, isSelected, hasFocus, row, column);
            }

            @Override
            protected Font computeFontStyle(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Font font = super.computeFontStyle(table, value, isSelected, hasFocus, row, column);
                if (column == 0 && _VERSION_TO_HIGHLIGHT.equals(table.getValueAt(row, 0)))
                    return font.deriveFont(Font.BOLD);
                return font;
            }
        };
        table.setDefaultRenderer(String.class, renderer);
        contentPnl.add(new JScrollPane(table), BorderLayout.CENTER);

        // also add filtering at the bottom
        JPanel filterPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        filterPnl.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        filterPnl.add(SeerGuiUtils.createLabel("Text: ", Font.BOLD));
        filterPnl.add(Box.createHorizontalStrut(5));
        final JTextField filterFld = new JTextField(25);
        filterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_OUT);
        filterPnl.add(filterFld);
        filterPnl.add(Box.createHorizontalStrut(20));

        final JButton applyBtn = SeerGuiUtils.createButton("Apply", "apply-software-filter", "Apply Filter", e -> table.applySearchTextAsRowFilter(filterFld.getText()));
        filterPnl.add(applyBtn);
        filterFld.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    applyBtn.doClick();
            }
        });
        filterPnl.add(Box.createHorizontalStrut(3));
        JButton resetBtn = SeerGuiUtils.createButton("Reset", "reset-software-filter", "Reset Filter", e -> {
            filterFld.setText(null);
            table.resetRowFilter();
        });
        filterPnl.add(resetBtn);
        contentPnl.add(filterPnl, BorderLayout.SOUTH);

        // give the focus to the filter field
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                filterFld.requestFocusInWindow();
                frame.removeComponentListener(this);
            }
        });

        // display the main frame
        SeerGuiUtils.showAndPosition(frame, null);
    }
}
