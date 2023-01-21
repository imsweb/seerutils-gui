/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class SeerMultiSelectComboBoxTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        //SeerGuiUtils.setFontDelta(5);

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1050, 300));

        PopupMenuListener listener = new SeerPopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                SeerMultiSelectComboBox<?> combo = (SeerMultiSelectComboBox<?>)e.getSource();
                System.out.println("From listener on " + combo.getName() + ": " + combo.getSelectedItems());
            }
        };

        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);

        JPanel centerPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));

        JPanel test1Pnl = SeerGuiUtils.createPanel();
        test1Pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        test1Pnl.add(SeerGuiUtils.createLabel("Test #1 (simple):", Font.BOLD));
        List<MyTestClass> list1 = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            list1.add(new MyTestClass(i));
        final SeerMultiSelectComboBox<MyTestClass> combo1 = new SeerMultiSelectComboBox<>(list1);
        combo1.setName("Combo #1");
        combo1.setMaximumRowCount(10); // no scrolling...
        combo1.addPopupMenuListener(listener);
        test1Pnl.add(combo1, BorderLayout.SOUTH);
        centerPnl.add(test1Pnl);

        JPanel test2Pnl = SeerGuiUtils.createPanel();
        test2Pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        test2Pnl.add(SeerGuiUtils.createLabel("Test #2 (scrolling):", Font.BOLD));
        List<String> list2 = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            list2.add("String #" + i);
        final SeerMultiSelectComboBox<String> combo2 = new SeerMultiSelectComboBox<>(list2);
        combo2.setName("Combo #2");
        combo2.setMaximumRowCount(7); // force scrolling
        combo2.addPopupMenuListener(listener);
        test2Pnl.add(combo2, BorderLayout.SOUTH);
        centerPnl.add(test2Pnl);

        JPanel test3Pnl = SeerGuiUtils.createPanel();
        test3Pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        test3Pnl.add(SeerGuiUtils.createLabel("Test #3 (separator):", Font.BOLD));
        List<String> list3 = new ArrayList<>();
        list3.add("Object 1");
        list3.add("Object 2");
        list3.add("Object 3");
        list3.add(null); // this is a separator
        list3.add("Object A");
        list3.add("Object B");
        list3.add(null); // this is a separator
        list3.add("Object i");
        list3.add("Object ii");
        final SeerMultiSelectComboBox<String> combo3 = new SeerMultiSelectComboBox<>(list3);
        combo3.setName("Combo #3");
        combo3.setMaximumRowCount(10); // no scrolling...
        combo3.addPopupMenuListener(listener);
        test3Pnl.add(combo3, BorderLayout.SOUTH);
        centerPnl.add(test3Pnl);

        JPanel test4Pnl = SeerGuiUtils.createPanel();
        test4Pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        test4Pnl.add(SeerGuiUtils.createLabel("Test #4 (all controls, pres-selection):", Font.BOLD));
        final List<String> list4 = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            list4.add("String #" + i);
        list4.add(null); // this is a separator
        list4.add("This is a much longer item in the list");
        final SeerMultiSelectComboBox<String> combo4 = new SeerMultiSelectComboBox<>(list4);
        combo4.setName("Combo #4");
        combo4.setMaximumRowCount(10); // no scrolling...
        combo4.addPopupMenuListener(listener);
        test4Pnl.add(combo4, BorderLayout.SOUTH);
        centerPnl.add(test4Pnl);

        JPanel test5Pnl = SeerGuiUtils.createPanel();
        test5Pnl.add(SeerGuiUtils.createLabel("Test #5 (customized text):", Font.BOLD));
        List<String> list5 = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            list5.add("String #" + i);
        final SeerMultiSelectComboBox<String> combo5 = new SeerMultiSelectComboBox<>(list5);
        combo5.setName("Combo #5");
        combo5.setMaximumRowCount(7); // no scrolling...
        combo5.setNoSelectionText("Select something now!");
        combo5.setNoSelectionForeground(Color.BLUE);
        combo5.setMultipleSelectionText("Now you are getting greedy!");
        combo5.setMultipleSelectionForeground(Color.RED);
        combo5.setOneForeground(Color.ORANGE);
        combo5.addPopupMenuListener(listener);
        test5Pnl.add(combo5, BorderLayout.SOUTH);
        centerPnl.add(test5Pnl);

        contentPnl.add(centerPnl, BorderLayout.CENTER);
        JPanel btnPnl = SeerGuiUtils.createPanel(new GridBagLayout());
        btnPnl.add(SeerGuiUtils.createButton("Print All Selections", "test", "test", e -> {
            System.out.println("Combo #1: " + combo1.getSelectedItems());
            System.out.println("Combo #2: " + combo2.getSelectedItems());
            System.out.println("Combo #3: " + combo3.getSelectedItems());
            System.out.println("Combo #4: " + combo4.getSelectedItems());
            System.out.println("Combo #5: " + combo5.getSelectedItems());
        }));
        contentPnl.add(btnPnl, BorderLayout.SOUTH);

        SeerGuiUtils.showAndPosition(frame, null);

        SwingUtilities.invokeLater(() -> {
            // selection can be done before showing the component, or like this, after showing it...
            combo4.setSelectedItems(Collections.singletonList(list4.get(3)));
        });
    }

    private static class MyTestClass {

        private Integer _value;

        public MyTestClass(Integer value) {
            _value = value;
        }

        @Override
        public String toString() {
            return "Item # " + _value;
        }
    }

}
