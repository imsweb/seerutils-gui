/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class SeerListTest {

    private static int _COUNTER = 0;

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //frame.setPreferredSize(new Dimension(900, 300));
        frame.setResizable(false);

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame, 0);
        contentPnl.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // NORTH - lists
        JPanel listPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 15, 0));
        contentPnl.add(listPnl, BorderLayout.NORTH);

        List<MyTestClass> list = new ArrayList<>();
        for (int i = 1; i < 50; i++)
            list.add(new MyTestClass("Object " + i));
        List<String> list2 = new ArrayList<>();
        list2.add(" hello (" + _COUNTER + ")");
        list2.add(" kitty (" + _COUNTER + ")");
        list2.add(" bye (" + _COUNTER + ")");
        list2.add(" meow (" + (_COUNTER++) + ")");
        Comparator<String> comp = String::compareToIgnoreCase;

        JPanel l0Pnl = SeerGuiUtils.createPanel();
        l0Pnl.add(SeerGuiUtils.createLabel("1:regular", Font.BOLD), BorderLayout.NORTH);
        SeerList<MyTestClass> l0 = new SeerList<>(list);
        l0Pnl.add(new JScrollPane(l0), BorderLayout.SOUTH);
        listPnl.add(l0Pnl);

        JPanel l1Pnl = SeerGuiUtils.createPanel();
        l1Pnl.add(SeerGuiUtils.createLabel("2:contained", Font.BOLD), BorderLayout.NORTH);
        SeerListModel<MyTestClass> myModel = new SeerListModel<MyTestClass>(list, SeerList.FILTERING_MODE_CONTAINED, null) {
            @Override
            protected boolean filterElement(MyTestClass element, String filter) {
                if (filter == null || filter.trim().isEmpty())
                    return true;

                if (element.toString().contains(filter))
                    return true;

                return false;
            }
        };
        final SeerList<MyTestClass> l1 = new SeerList<>(myModel, SeerList.DISPLAY_MODE_DOTTED_LINES, false);
        l1Pnl.add(new JScrollPane(l1), BorderLayout.SOUTH);
        listPnl.add(l1Pnl);

        JPanel l2Pnl = SeerGuiUtils.createPanel();
        l2Pnl.add(SeerGuiUtils.createLabel("3:starts with Object 2", Font.BOLD), BorderLayout.NORTH);
        SeerList<MyTestClass> l2 = new SeerList<>(list, SeerList.DISPLAY_MODE_ALT_COLORS, SeerList.FILTERING_MODE_STARTS_WITH);
        l2.filter("Object 2");
        l2Pnl.add(new JScrollPane(l2), BorderLayout.SOUTH);
        listPnl.add(l2Pnl);

        JPanel l3Pnl = SeerGuiUtils.createPanel();
        l3Pnl.add(SeerGuiUtils.createLabel("4:equals Object 18", Font.BOLD), BorderLayout.NORTH);
        SeerList<MyTestClass> l3 = new SeerList<>(list, SeerList.DISPLAY_MODE_NONE, SeerList.FILTERING_MODE_EQUALS, true, null);
        l3.filter("Object 18");
        l3Pnl.add(new JScrollPane(l3), BorderLayout.SOUTH);
        listPnl.add(l3Pnl);

        JPanel l4Pnl = SeerGuiUtils.createPanel();
        l4Pnl.add(SeerGuiUtils.createLabel("5:comparator enabled", Font.BOLD), BorderLayout.NORTH);
        final SeerList<String> l4 = new SeerList<>(list2, SeerList.DISPLAY_MODE_NONE, SeerList.FILTERING_MODE_STARTS_WITH, false, comp);
        l4.filter(" ");
        l4Pnl.add(new JScrollPane(l4), BorderLayout.SOUTH);
        listPnl.add(l4Pnl);

        final JTextField myFilter = new JTextField(15);
        JButton applyFilterBtn = SeerGuiUtils.createButton("Apply", "test", "test", e -> l1.filter(myFilter.getText()));
        JButton resetFilterBtn = SeerGuiUtils.createButton("Reset", "test", "test", e -> l1.resetFilter());
        SeerGuiUtils.synchronizedComponentsWidth(applyFilterBtn, resetFilterBtn);

        JButton resetDataBtn = SeerGuiUtils.createButton("Do it!", "test", "test", e -> {
            List<String> newList = new ArrayList<>();
            newList.add(" hello (" + _COUNTER + ")");
            newList.add(" kitty (" + _COUNTER + ")");
            newList.add(" bye (" + _COUNTER + ")");
            newList.add(" meow (" + (_COUNTER++) + ")");
            l4.resetData(newList);
        });

        // SOUTH - controls
        JPanel ctrlPnl = SeerGuiUtils.createPanel();
        ctrlPnl.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        contentPnl.add(ctrlPnl, BorderLayout.SOUTH);
        JPanel ctrlLeftPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        ctrlLeftPnl.add(SeerGuiUtils.createLabel("For #2  type a filter:", Font.BOLD));
        ctrlLeftPnl.add(Box.createHorizontalStrut(5));
        ctrlLeftPnl.add(myFilter);
        ctrlLeftPnl.add(Box.createHorizontalStrut(5));
        ctrlLeftPnl.add(applyFilterBtn);
        ctrlLeftPnl.add(Box.createHorizontalStrut(5));
        ctrlLeftPnl.add(resetFilterBtn);
        ctrlPnl.add(ctrlLeftPnl, BorderLayout.WEST);

        JPanel ctrlRightPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        ctrlRightPnl.add(SeerGuiUtils.createLabel("Reset Data:", Font.BOLD));
        ctrlRightPnl.add(Box.createHorizontalStrut(5));
        ctrlRightPnl.add(resetDataBtn);
        ctrlPnl.add(ctrlRightPnl, BorderLayout.EAST);

        SeerGuiUtils.showAndPosition(frame, null);
    }

    private static class MyTestClass {

        private String _s;

        public MyTestClass(String s) {
            _s = s;
        }

        @Override
        public String toString() {
            return _s;
        }
    }
}
