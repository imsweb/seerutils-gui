/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class SeerCardPanelTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();

        SeerGuiUtils.setFontDelta(0);

        JFrame myFrame = new JFrame("SeerCardPanelTest");
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setPreferredSize(new Dimension(600, 400));

        //panels and buttons
        JPanel myMainPnl = SeerGuiUtils.createContentPanel(myFrame);
        final SeerCardPanel myContentPnl = new SeerCardPanel();
        final JLabel myIdLabel = new JLabel("page id page id page id");
        myIdLabel.setOpaque(true);
        myIdLabel.setBackground(Color.RED);
        JPanel myButtonPnl = new JPanel();
        JButton addPage = new JButton("add page");
        JButton showPage = new JButton("show page");
        final JTextField whichId = new JTextField(5);
        final JTextField newId = new JTextField(5);
        JButton pageId = new JButton("page ID");
        myButtonPnl.add(addPage);
        myButtonPnl.add(newId);
        myButtonPnl.add(showPage);
        myButtonPnl.add(whichId);
        myButtonPnl.add(pageId);

        // button actions
        class MyBtnAddPage implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                String id = newId.getText();
                myContentPnl.addPage(id, new JPanel());
                myIdLabel.setText(id + " has been added");
            }
        }
        class MyBtnShowPage implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                String id = whichId.getText();
                myContentPnl.showPage(id);
                myIdLabel.setText("the page currently being shown is " + id);
            }
        }
        class MyBtnGetId implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                String currentId = myContentPnl.getCurrentPageId();
                //myIdLabel.setText(currentId);
                myIdLabel.setText("this page is page #" + currentId);
            }
        }
        addPage.addActionListener(new MyBtnAddPage());
        showPage.addActionListener(new MyBtnShowPage());
        pageId.addActionListener(new MyBtnGetId());

        //set visible
        myMainPnl.add(myIdLabel, BorderLayout.NORTH);
        myMainPnl.add(myContentPnl, BorderLayout.CENTER);
        myMainPnl.add(myButtonPnl, BorderLayout.SOUTH);
        SeerGuiUtils.showAndPosition(myFrame, null);

    }

}
