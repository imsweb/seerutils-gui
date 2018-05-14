/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class SeerBoldTitlesTabbedPaneTest {

    public static final int LB_ANCHOR = GridBagConstraints.FIRST_LINE_START;
    public static final int BTN_ANCHOR = GridBagConstraints.FIRST_LINE_END;

    public static void main(String[] args) {

        //Frame and contentPnl setup
        SeerGuiUtils.setupGuiEnvForSeerProject();
        final JFrame frame = new JFrame("Test");
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel contentPnl = SeerGuiUtils.createContentPanel(frame);
        contentPnl.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 20));

        //General layout setup
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        EmptyBorder borderInner = (EmptyBorder)BorderFactory.createEmptyBorder(15, 5, 0, 5);

        //TOP-RIGHT:original pane  BOTTOM-RIGHT get page pane
        final SeerBoldTitlesTabbedPane pane = new SeerBoldTitlesTabbedPane();
        pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pane.setSynchronizeHeaderWidths(true);
        pane.setCenterTitles(true); // this is the default
        pane.setHeaderBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        pane.addPage("TAB 1", new TestPage(pane, 1));
        pane.addPage("TAB 2", new TestPage(pane, 1), 1);
        pane.addPage(new SeerBoldTitlesTabbedPaneHeader("TAB 3", 1), new TestPage(pane, 3));
        pane.addPage(new SeerBoldTitlesTabbedPaneHeader("TAB 4", 2, SeerGuiUtils.createIcon("watermelon.jpg"), BorderFactory.createEmptyBorder(2, 4, 2, 4), false), new TestPage(pane, 4));
        pane.addPage(new SeerBoldTitlesTabbedPaneHeader("TAB 5", 3, SeerGuiUtils.createIcon("strawberry.jpg"), BorderFactory.createEmptyBorder(2, 4, 2, 4), true, true), new TestPage(pane, 5));
        pane.addPage(new SeerBoldTitlesTabbedPaneHeader("TAB 6 a really long title", 4, SeerGuiUtils.createIcon("grape.jpg")), new TestPage(pane, 6), 1);

        final SeerBoldTitlesTabbedPane panePage = new SeerBoldTitlesTabbedPane();
        panePage.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        SeerBoldTitlesTabbedPanePage testPage = new SeerBoldTitlesTabbedPanePage(panePage);
        testPage.add(SeerGuiUtils.createLabel("<html>Use get page to fetch a page from the top panel to this one. <br/>" + "Once Fetched, the page cannot be added back</html>", Font.BOLD, 22,
                Color.ORANGE));
        testPage.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panePage.addPage(new SeerBoldTitlesTabbedPaneHeader("Info", 2, SeerGuiUtils.createIcon("watermelon.jpg"), BorderFactory.createEmptyBorder(2, 4, 2, 4), false), testPage);

        //LEFT: ctrl panel
        JPanel ctrlPnl = SeerGuiUtils.createPanel(new GridLayout(7, 1));
        ctrlPnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //ctrlPnl(1):display
        JPanel displayPnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder displayTtl = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Display", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(
                "serif", Font.BOLD, 12), Color.yellow);
        displayPnl.setBorder(BorderFactory.createCompoundBorder(displayTtl, borderInner));
        JLabel lbDisplayByName = SeerGuiUtils.createLabel("Name", 1, Color.yellow);
        JLabel lbDisplayByIdx = SeerGuiUtils.createLabel("Idx");
        final JTextField txDisplayByName = new JTextField(8);
        final JTextField txDisplayByIdx = new JTextField(4);
        JButton btnDisplayByName = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.displayPage(txDisplayByName.getText()));
        JButton btnDisplayByIdx = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.displayPage(Integer.parseInt(txDisplayByIdx.getText())));
        JButton btnPrevPage = SeerGuiUtils.createButton("<<", "before", "Previous Page", e -> pane.displayPreviousPage());
        JButton btnNextPage = SeerGuiUtils.createButton(">>", "after", "Next Page", e -> pane.displayNextPage());
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        displayPnl.add(lbDisplayByName, c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        displayPnl.add(txDisplayByName, c);
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = BTN_ANCHOR;
        displayPnl.add(btnDisplayByName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        displayPnl.add(lbDisplayByIdx, c);
        c.gridx = 1;
        c.gridy = 1;
        displayPnl.add(txDisplayByIdx, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = BTN_ANCHOR;
        displayPnl.add(btnDisplayByIdx, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = LB_ANCHOR;
        displayPnl.add(btnPrevPage, c);
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = BTN_ANCHOR;
        displayPnl.add(btnNextPage, c);

        //ctrlpnl(2) disable panel
        JPanel disablePnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder disableTtl = BorderFactory.createTitledBorder("Disable");
        disablePnl.setBorder(BorderFactory.createCompoundBorder(disableTtl, borderInner));
        JLabel lbDisableByName = SeerGuiUtils.createLabel("Name");
        JLabel lbDisableByIdx = SeerGuiUtils.createLabel("Idx");
        JLabel lbDisableAll = SeerGuiUtils.createLabel("All");
        final JTextField txDisableByName = new JTextField(8);
        final JTextField txDisableByIdx = new JTextField(4);
        JButton btnDisableByName = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.disablePage(txDisableByName.getText()));
        JButton btnDisableByIdx = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.disablePage(Integer.parseInt(txDisableByIdx.getText())));
        JButton btnDisableAll = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.disableAllPages());
        lbDisableAll.setOpaque(true);
        lbDisableByName.setOpaque(true);
        lbDisableByIdx.setOpaque(true);
        lbDisableAll.setBackground(Color.pink);
        lbDisableByName.setBackground(Color.pink);
        lbDisableByIdx.setBackground(Color.pink);
        lbDisableAll.setHorizontalAlignment(SwingConstants.LEFT);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        disablePnl.add(lbDisableByName, c);
        c.gridx = 1;
        c.gridy = 0;
        disablePnl.add(txDisableByName, c);
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = BTN_ANCHOR;
        disablePnl.add(btnDisableByName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        disablePnl.add(lbDisableByIdx, c);
        c.gridx = 1;
        c.gridy = 1;
        disablePnl.add(txDisableByIdx, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = BTN_ANCHOR;
        disablePnl.add(btnDisableByIdx, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = LB_ANCHOR;
        disablePnl.add(lbDisableAll, c);
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = BTN_ANCHOR;
        disablePnl.add(btnDisableAll, c);

        //ctrlPnl (3) enable panel
        JPanel enablePnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder enableTtl = BorderFactory.createTitledBorder("Enable");
        enablePnl.setBorder(BorderFactory.createCompoundBorder(enableTtl, borderInner));
        JLabel lbEnableByName = SeerGuiUtils.createLabel("Name");
        JLabel lbEnableByIdx = SeerGuiUtils.createLabel("Idx");
        JLabel lbEnableAll = SeerGuiUtils.createLabel("All");
        final JTextField txEnableByName = new JTextField(8);
        final JTextField txEnableByIdx = new JTextField(4);
        JButton btnEnableByName = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.enablePage(txEnableByName.getText()));
        JButton btnEnableByIdx = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.enablePage(Integer.parseInt(txEnableByIdx.getText())));
        JButton btnEnableAll = SeerGuiUtils.createButton("GO", "test", "test", e -> pane.enableAllPages());
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        enablePnl.add(lbEnableByName, c);
        c.gridx = 1;
        c.gridy = 0;
        enablePnl.add(txEnableByName, c);
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = BTN_ANCHOR;
        enablePnl.add(btnEnableByName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        enablePnl.add(lbEnableByIdx, c);
        c.gridx = 1;
        c.gridy = 1;
        enablePnl.add(txEnableByIdx, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = BTN_ANCHOR;
        enablePnl.add(btnEnableByIdx, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = LB_ANCHOR;
        enablePnl.add(lbEnableAll, c);
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = BTN_ANCHOR;
        enablePnl.add(btnEnableAll, c);

        //ctrlPnl(4): getter
        JPanel getterPnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder getterTtl = BorderFactory.createTitledBorder("Get Name/Idx");
        getterPnl.setBorder(BorderFactory.createCompoundBorder(getterTtl, borderInner));
        JLabel lbGetIdxByName = SeerGuiUtils.createLabel("Name");
        JLabel lbGetNameByIdx = SeerGuiUtils.createLabel("Idx");
        JLabel lbNameIs = SeerGuiUtils.createLabel("Name:");
        JLabel lbIdxIs = SeerGuiUtils.createLabel("Idx:");
        final JLabel lbPageName = SeerGuiUtils.createLabel("       ");
        final JLabel lbPageIdx = SeerGuiUtils.createLabel("     ");
        final JTextField txGetIdxByName = new JTextField(8);
        final JTextField txGetNameByIdx = new JTextField(4);
        JButton btnGetIdxByName = SeerGuiUtils.createButton("GET Idx", "test", "test", e -> {
            lbPageName.setText(txGetIdxByName.getText());
            lbPageIdx.setText(Integer.toString(pane.getPageIndex(txGetIdxByName.getText())));
        });
        JButton btnGetNameByIdx = SeerGuiUtils.createButton("GET Name", "test", "test", e -> {
            lbPageIdx.setText(txGetIdxByName.getText());
            lbPageName.setText(pane.getPageTitle(Integer.parseInt(txGetNameByIdx.getText())));
        });
        JButton btnGetCurrentPageNameAndIdx = SeerGuiUtils.createButton("Current", "test", "test", e -> {
            lbPageIdx.setText(Integer.toString(pane.getCurrentPageIndex()));
            lbPageName.setText(pane.getCurrentPageTitle());
        });
        SeerGuiUtils.synchronizedComponentsWidth(btnGetNameByIdx, btnGetIdxByName, btnGetCurrentPageNameAndIdx);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        getterPnl.add(lbGetIdxByName, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 2;
        getterPnl.add(txGetIdxByName, c);
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        getterPnl.add(btnGetIdxByName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        getterPnl.add(lbGetNameByIdx, c);
        c.gridx = 1;
        c.gridy = 1;
        getterPnl.add(txGetNameByIdx, c);
        c.gridx = 4;
        c.gridy = 1;
        getterPnl.add(btnGetNameByIdx, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = LB_ANCHOR;
        getterPnl.add(lbNameIs, c);
        c.gridx = 1;
        c.gridy = 2;
        getterPnl.add(lbPageName, c);
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        getterPnl.add(lbIdxIs, c);
        c.gridx = 3;
        c.gridy = 2;
        getterPnl.add(lbPageIdx, c);
        c.gridx = 4;
        c.gridy = 2;
        getterPnl.add(btnGetCurrentPageNameAndIdx, c);

        //ctrlPnl(5): getPage
        JPanel getPagePnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder getPageTtl = BorderFactory.createTitledBorder("Get Page");
        getPagePnl.setBorder(BorderFactory.createCompoundBorder(getPageTtl, borderInner));
        JLabel lbGetPageByName = SeerGuiUtils.createLabel("Name");
        JLabel lbGetPageByIdx = SeerGuiUtils.createLabel("Idx");
        final JTextField txGetPageByName = new JTextField(8);
        final JTextField txGetPageByIdx = new JTextField(4);
        JButton btnGetPageByName = SeerGuiUtils.createButton("GET", "test", "test", e -> panePage.addPage("this page is", pane.getPage(txGetPageByName.getText())));
        JButton btnGetPageByIdx = SeerGuiUtils.createButton("GET", "test", "test", e -> panePage.addPage("the page is", pane.getPage(Integer.parseInt(txGetPageByIdx.getText()))));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        getPagePnl.add(lbGetPageByName, c);
        c.gridx = 1;
        c.gridy = 0;
        getPagePnl.add(txGetPageByName, c);
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = BTN_ANCHOR;
        getPagePnl.add(btnGetPageByName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        getPagePnl.add(lbGetPageByIdx, c);
        c.gridx = 1;
        c.gridy = 1;
        getPagePnl.add(txGetPageByIdx, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = BTN_ANCHOR;
        getPagePnl.add(btnGetPageByIdx, c);

        //ctrlPnl(6): addRemove
        JPanel addRemovePnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder addRemoveTtl = BorderFactory.createTitledBorder("Add/Remove");
        addRemovePnl.setBorder(BorderFactory.createCompoundBorder(addRemoveTtl, borderInner));
        JLabel lbAddPageByIdx = SeerGuiUtils.createLabel("Idx");
        JLabel lbAddPageByName = SeerGuiUtils.createLabel("Name");
        JLabel lbRemovePageByName = SeerGuiUtils.createLabel("Name");
        final JTextField txAddPageByIdx = new JTextField(4);
        final JTextField txAddPageByName = new JTextField(8);
        final JTextField txRemovePageByName = new JTextField(8);
        JButton btnAddPage = SeerGuiUtils.createButton("ADD", "test", "test", e -> pane.addPage(txAddPageByName.getText(), new TestPage(pane, Integer.parseInt(txAddPageByIdx.getText()))));
        JButton btnRemovePage = SeerGuiUtils.createButton("REMOVE", "test", "test", e -> System.out.println(pane.removePage(txRemovePageByName.getText())));
        SeerGuiUtils.synchronizedComponentsWidth(btnAddPage, btnRemovePage);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        addRemovePnl.add(lbAddPageByName, c);
        c.gridx = 1;
        c.gridy = 0;
        addRemovePnl.add(txAddPageByName, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        addRemovePnl.add(lbAddPageByIdx, c);
        c.gridx = 1;
        c.gridy = 1;
        addRemovePnl.add(txAddPageByIdx, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = BTN_ANCHOR;
        addRemovePnl.add(btnAddPage, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = LB_ANCHOR;
        addRemovePnl.add(lbRemovePageByName, c);
        c.gridx = 1;
        c.gridy = 2;
        addRemovePnl.add(txRemovePageByName, c);
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = BTN_ANCHOR;
        addRemovePnl.add(btnRemovePage, c);

        //ctrlPanel(7): edit headers
        JPanel editHeadersPnl = SeerGuiUtils.createPanel(new GridBagLayout());
        TitledBorder editHeadersTtl = BorderFactory.createTitledBorder("Edit Title");
        editHeadersPnl.setBorder(BorderFactory.createCompoundBorder(editHeadersTtl, borderInner));
        JLabel lblEditHeaderByIdx = SeerGuiUtils.createLabel("Idx");
        JLabel lblEditHeaderNewName = SeerGuiUtils.createLabel("Name");
        final JTextField txEditHeaderByIdx = new JTextField(4);
        final JTextField txEditHeaderNewName = new JTextField(8);
        JButton btnEditHeader = SeerGuiUtils.createButton("EDIT", "test", "test", e -> pane.updateHeader(Integer.parseInt(txEditHeaderByIdx.getText()), txEditHeaderNewName.getText()));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = LB_ANCHOR;
        editHeadersPnl.add(lblEditHeaderByIdx, c);
        c.gridx = 1;
        c.gridy = 0;
        editHeadersPnl.add(txEditHeaderByIdx, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = LB_ANCHOR;
        editHeadersPnl.add(lblEditHeaderNewName, c);
        c.gridx = 1;
        c.gridy = 1;
        editHeadersPnl.add(txEditHeaderNewName, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = BTN_ANCHOR;
        editHeadersPnl.add(btnEditHeader, c);

        //add components together
        ctrlPnl.add(displayPnl);
        ctrlPnl.add(disablePnl);
        ctrlPnl.add(enablePnl);
        ctrlPnl.add(getterPnl);
        ctrlPnl.add(getPagePnl);
        ctrlPnl.add(addRemovePnl);
        ctrlPnl.add(editHeadersPnl);
        JPanel centerPnl = SeerGuiUtils.createPanel();
        centerPnl.add(pane, BorderLayout.CENTER);
        centerPnl.add(panePage, BorderLayout.SOUTH);
        contentPnl.add(centerPnl, BorderLayout.CENTER);
        contentPnl.add(ctrlPnl, BorderLayout.WEST);
        SeerGuiUtils.showAndPosition(frame, null);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                //pane.disableAllPages();
                //pane.disablePage("Page 2");
                pane.disablePage(1);
                frame.removeComponentListener(this);
            }
        });
    }

    private static class TestPage extends SeerBoldTitlesTabbedPanePage {

        public TestPage(SeerBoldTitlesTabbedPane parent, int idx) {
            super(parent);
            this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            this.setLayout(new BorderLayout());
            this.add(SeerGuiUtils.createLabel("This is page #" + idx, Font.PLAIN, 16, Color.GRAY), BorderLayout.NORTH);
            this.add(SeerGuiUtils.createLabel("<html>Feature List:<br/> " +
                    "1.Simple style<br/>" +
                    "2.Simple sytle:Assigned Idx<br/>" +
                    "3.SeerTabbedPaneHeaders<br>" +
                    "4.SeerTabbedPaneHeaders:Icon, Current<br/>" +
                    "5.SeerTabbedPaneHeaders:Icon, Centered title<br/>" +
                    "6.SeerTabbedPaneHeaders:Icon, Assigned Idx</html>", Font.PLAIN, 16, Color.DARK_GRAY), BorderLayout.CENTER);
            this.add(SeerGuiUtils.createLabel("<html>Possible Bugs:<br/>" +
                    "1. Add/Remove: Tabs with the same Name can be added and the name will show on each tab.<br/> " +
                    "However, only one of them will actually have that name on the backend.<br/> " +
                    "When removed by Name, only that one tab will be removed.<br/>" +
                    "2. get page by name method: getPage(String name)seems not working" +
                    "3.</html>", Font.PLAIN, 16, Color.RED), BorderLayout.SOUTH);
        }
    }
}
