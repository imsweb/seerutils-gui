/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SeerOptionalSidesLineBorderTest {

    public static void main(String[] args) {
        
        JFrame myFrame = new JFrame("TEST");
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setPreferredSize(new Dimension(1000, 600));
        JPanel myContentPnl = SeerGuiUtils.createContentPanel(myFrame);
        GridLayout borderTest = new GridLayout(2, 2);
        myContentPnl.setLayout(borderTest);
        JPanel myPnl1 = new JPanel();
        JPanel myPnl2 = new JPanel();
        JPanel myPnl3 = new JPanel();
        JPanel myPnl4 = new JPanel();
        myPnl1.setOpaque(true);
        myPnl1.setBackground(Color.WHITE);
        myPnl2.setOpaque(true);
        myPnl2.setBackground(Color.ORANGE);
        myPnl3.setOpaque(true);
        myPnl3.setBackground(Color.ORANGE);
        myPnl4.setOpaque(true);
        myPnl4.setBackground(Color.WHITE);
        myContentPnl.add(myPnl1);
        myContentPnl.add(myPnl2);
        myContentPnl.add(myPnl3);
        myContentPnl.add(myPnl4);
        JLabel lb1 = new JLabel("top, left,red");
        JLabel lb2 = new JLabel("left, bottom,black");
        JLabel lb3 = new JLabel("top, right,black");
        JLabel lb4 = new JLabel("bottom, right,red");
        myPnl1.add(lb1);
        myPnl2.add(lb2);
        myPnl3.add(lb3);
        myPnl4.add(lb4);

        //border styles
        SeerOptionalSidesLineBorder border1 = new SeerOptionalSidesLineBorder(Color.RED, true, true, false, false);
        SeerOptionalSidesLineBorder border2 = new SeerOptionalSidesLineBorder(Color.BLACK, false, true, true, false);
        SeerOptionalSidesLineBorder border3 = new SeerOptionalSidesLineBorder(Color.BLACK, true, false, false, true);
        SeerOptionalSidesLineBorder border4 = new SeerOptionalSidesLineBorder(Color.RED, false, false, true, true);

        //apply border styles
        myPnl1.setBorder(border1);
        myPnl2.setBorder(border2);
        myPnl3.setBorder(border3);
        myPnl4.setBorder(border4);

        SeerGuiUtils.showAndPosition(myFrame, null);

    }
}
