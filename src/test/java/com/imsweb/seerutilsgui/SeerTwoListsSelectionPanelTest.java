/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.math.NumberUtils;

public class SeerTwoListsSelectionPanelTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        SeerGuiUtils.setFontDelta(0);

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 400));

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        final List<MyTestClass> left = new ArrayList<>();
        left.add(new MyTestClass("Maine", 1095));
        left.add(new MyTestClass("New Hampshire", 892));
        left.add(new MyTestClass("Vermont", 1));
        left.add(new MyTestClass("Massachusetts", 10));
        left.add(new MyTestClass("Connecticut", 62893));
        left.add(new MyTestClass("Rhode Island", 3095));
        left.add(new MyTestClass("New Jersey", 392));
        left.add(new MyTestClass("New York", 4));
        left.add(new MyTestClass("Pennsylvania", 80));
        left.add(new MyTestClass("Maryland", 72893));
        left.add(new MyTestClass("Virginia", "Not an Integer"));
        left.add(new MyTestClass("North Carolina", "Testing"));
        left.add(new MyTestClass("South Carolina", "Still not an Integer"));
        left.add(new MyTestClass("Georgia", "Hello"));
        left.add(new MyTestClass("Delaware", "Argh"));
        final List<MyTestClass> right = new ArrayList<>();
        right.add(new MyTestClass("Alaska", 1094));
        right.add(new MyTestClass("Hawaii", 892));
        right.add(new MyTestClass("Washington", 1));
        right.add(new MyTestClass("Oregon", 10));
        right.add(new MyTestClass("California", 62893));
        right.add(new MyTestClass("Nevada", 3095));
        right.add(new MyTestClass("Arizona", 392));
        right.add(new MyTestClass("Idaho", 4));
        right.add(new MyTestClass("Montana", 80));
        right.add(new MyTestClass("Wyoming", 72893));
        right.add(new MyTestClass("Colorado", "Not an Integer"));
        right.add(new MyTestClass("New Mexico", "Testing"));
        right.add(new MyTestClass("Texas", "Still not an Integer"));
        right.add(new MyTestClass("Oklahoma", "Hello"));
        right.add(new MyTestClass("Kansas", "Argh"));
        //  right.add(new MyTestClass("Object A"));
        // right.add(new MyTestClass("Object B"));
        //right.add(new MyTestClass("Object C"));

        JLabel leftLbl = SeerGuiUtils.createLabel("Available:", Font.BOLD);
        JLabel rightLbl = SeerGuiUtils.createLabel("Selected:", Font.BOLD);
        // note that if a right comparator is provided, then the up/down buttons won't be displayed (because they would compete with the comparator's order)
        Comparator<MyTestClass> comp = Comparator.comparing(MyTestClass::toString);
        SeerListModel<MyTestClass> myLeftModel = new SeerListModel<MyTestClass>(left, SeerList.FILTERING_MODE_CONTAINED, comp) {
            @Override
            protected boolean filterElement(MyTestClass element, String filter) {
                return includeForFilter(element, filter);
            }
        };

        SeerListModel<MyTestClass> myRightModel = new SeerListModel<MyTestClass>(right, SeerList.FILTERING_MODE_CONTAINED, comp) {
            @Override
            protected boolean filterElement(MyTestClass element, String filter) {
                return includeForFilter(element, filter);
            }
        };
        final SeerTwoListsSelectionPanel<MyTestClass> pnl = new SeerTwoListsSelectionPanel<>(myLeftModel, myRightModel, leftLbl, rightLbl, true, false);
        //final SeerTwoListsSelectionPanel<MyTestClass> pnl = new SeerTwoListsSelectionPanel<MyTestClass>(left, right);

        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        contentPnl.add(pnl, BorderLayout.CENTER);

        JPanel controlsPnl = SeerGuiUtils.createPanel();
        controlsPnl.add(SeerGuiUtils.createButton("PRINT LISTS", "print", "Print", e -> {
            System.out.println("LEFT: " + pnl.getLeftListContent());
            System.out.println("RIGHT: " + pnl.getRightListContent());
        }), BorderLayout.NORTH);
        controlsPnl.add(SeerGuiUtils.createButton("RESET", "reset", "Reset", e -> {
            pnl.performResetLeftFilter();
            pnl.getLeftList().resetData(left);
            pnl.performResetRightFilter();
            pnl.getRightList().resetData(right);
        }), BorderLayout.SOUTH);
        contentPnl.add(controlsPnl, BorderLayout.SOUTH);

        SeerGuiUtils.showAndPosition(frame, null);
    }

    private static boolean includeForFilter(MyTestClass element, String filter) {
        String s = element.getFieldName().toLowerCase();
        Integer naaccr = element.getNaaccrID();

        boolean add = false;
        if (filter == null || filter.isEmpty())
            add = true;
        else {
            String filterLower = filter.toLowerCase();
            String[] filters;
            filters = filterLower.split("[,\\s]+");
            for (String filterElement : filters) {
                if (NumberUtils.isDigits(filterElement)) {
                    if (Integer.valueOf(filterElement).equals(naaccr))
                        add = true;
                }
                else if (s.contains(filterElement))
                    add = true;
            }
        }

        return add;
    }

    private static class MyTestClass {

        private String _s;
        private Object _naaccr;

        public MyTestClass(String s, Object naaccr) {
            _s = s;
            _naaccr = naaccr;
        }

        @Override
        public String toString() {
            if (this._naaccr instanceof Integer)
                return _s + " (#" + _naaccr + ")";
            else
                return _s + " (" + _naaccr + ")";
        }

        public Integer getNaaccrID() {
            if (this._naaccr instanceof Integer)
                return (Integer)this._naaccr;
            else
                return null;
        }

        public String getFieldName() {
            return this._s;
        }
    }
}
