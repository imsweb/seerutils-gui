/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class SeerBoldTitlesTabbedPane extends JTabbedPane {

    // whether the headers should be forced to have the same width (defaults to false)
    protected boolean _synchronizeHeaderWidth;

    // whether the lable should be centered in the headers (defaults to true)
    protected boolean _centerTitles;

    // the border to use for the headers (default to a small empty border)
    protected Border _titleBorder;

    // the current tab that is being displayed
    protected int _currentTabIndex;

    // the pages displayed in the tabs per header title
    protected Map<String, Component> _tabs = new HashMap<>();

    // the tab indexes per header title
    protected Map<String, Integer> _tabIndexes = new HashMap<>();

    // the headers per header title
    protected Map<String, SeerBoldTitlesTabbedPaneHeader> _headers = new HashMap<>();

    /**
     * Constructor.
     */
    public SeerBoldTitlesTabbedPane() {

        this.setName("tabbed-pane");
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder());

        _currentTabIndex = 0;
        _synchronizeHeaderWidth = false;
        _centerTitles = true;
        _titleBorder = BorderFactory.createEmptyBorder(2, 4, 2, 4);

        this.addChangeListener(e -> {
            if (isVisible()) {
                int showingTabIndex = getSelectedIndex();
                if (showingTabIndex != -1 && showingTabIndex != _currentTabIndex) {
                    for (int i = 0; i < getTabCount(); i++) {
                        SeerBoldTitlesTabbedPaneHeader header = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(i);
                        if (header != null)
                            header.setCurrent(i == showingTabIndex);
                    }

                    _currentTabIndex = showingTabIndex;
                }

                applyExtraStateChangedLogic();
            }
        });
    }

    /**
     * Whether the different headers should be forced to have the same width.
     * @param synchronizeHeaderWidth header width
     */
    public void setSynchronizeHeaderWidths(boolean synchronizeHeaderWidth) {
        _synchronizeHeaderWidth = synchronizeHeaderWidth;
    }

    /**
     * Whether the titles should be centered in the headers; this has no effect if customized headers are used.
     * @param centerTitles center titles
     */
    public void setCenterTitles(boolean centerTitles) {
        _centerTitles = centerTitles;
    }

    /**
     * The border to use for the headers; this has no effect if customized headers are used.
     * @param titleBorder title border
     */
    public void setHeaderBorder(Border titleBorder) {
        _titleBorder = titleBorder;
    }

    public void addPage(SeerBoldTitlesTabbedPaneHeader header, Component page) {
        addPage(header, null, page, getTabCount());
    }

    public void addPage(SeerBoldTitlesTabbedPaneHeader header, Component page, int idx) {
        addPage(header, null, page, idx);
    }

    public void addPage(String title, Component page) {
        addPage(null, title, page, getTabCount());
    }

    public void addPage(String title, Component page, int idx) {
        addPage(null, title, page, idx);
    }

    protected void addPage(SeerBoldTitlesTabbedPaneHeader header, String title, Component page, int idx) {

        if (header == null)
            header = new SeerBoldTitlesTabbedPaneHeader(title, idx, null, _titleBorder, _centerTitles, getTabCount() == 0);
        if (title == null)
            title = header.getHeaderTitle();

        if (_tabs.containsKey(title))
            throw new RuntimeException("Duplicate titles are not allowed!");

        insertTab(title, null, page, null, idx);
        setTabComponentAt(idx, header);

        _tabs.put(title, page);
        _headers.put(title, header);
        _tabIndexes.put(title, idx);

        if (_synchronizeHeaderWidth) {
            int maxWidth = 0;
            for (int i = 0; i < getTabCount(); i++) {
                SeerBoldTitlesTabbedPaneHeader pnl = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(i);
                maxWidth = Math.max(maxWidth, SwingUtilities.computeStringWidth(pnl.getFontMetrics(SeerGuiUtils.createLabel("", Font.BOLD).getFont()), pnl.getHeaderTitle()));
            }
            for (int i = 0; i < getTabCount(); i++)
                getTabComponentAt(i).setPreferredSize(new Dimension(maxWidth, getTabComponentAt(i).getPreferredSize().height));
        }

        if (isVisible()) {
            int showingTabIndex = getSelectedIndex();
            if (showingTabIndex != -1 && showingTabIndex != _currentTabIndex)
                _currentTabIndex = showingTabIndex;
        }
    }

    public boolean removePage(String title) {
        Component tab = _tabs.get(title);
        if (tab == null)
            return false;

        remove(tab);

        _tabs.remove(title);
        _headers.remove(title);
        _tabIndexes.remove(title);

        _currentTabIndex++;
        if (_currentTabIndex >= getTabCount())
            _currentTabIndex = getTabCount() == 0 ? -1 : 0;

        if (isVisible()) {
            int showingTabIndex = getSelectedIndex();
            if (showingTabIndex != -1 && showingTabIndex != _currentTabIndex)
                _currentTabIndex = showingTabIndex;
        }

        return true;
    }

    public void displayNextPage() {
        int tabIdx = getSelectedIndex();
        tabIdx++;
        if (tabIdx == getTabCount())
            tabIdx = 0;

        // never display a disable page
        while (tabIdx != getSelectedIndex()) {
            SeerBoldTitlesTabbedPaneHeader header = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(tabIdx);
            if (!header.isHeaderEnabled()) {
                tabIdx++;
                if (tabIdx == getTabCount())
                    tabIdx = 0;
            }
            else
                break;
        }

        setSelectedIndex(tabIdx);
        _currentTabIndex = tabIdx;
    }

    public void displayPreviousPage() {
        int tabIdx = getSelectedIndex();
        tabIdx--;
        if (tabIdx < 0)
            tabIdx = getTabCount() - 1;

        // never display a disable page
        while (tabIdx != getSelectedIndex()) {
            SeerBoldTitlesTabbedPaneHeader header = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(tabIdx);
            if (!header.isHeaderEnabled()) {
                tabIdx--;
                if (tabIdx < 0)
                    tabIdx = getTabCount() - 1;
            }
            else
                break;
        }

        setSelectedIndex(tabIdx);
        _currentTabIndex = tabIdx;
    }

    public void displayPage(String title) {
        Component tab = _tabs.get(title);
        if (tab != null)
            displayPage(indexOfComponent(tab));
    }

    public void displayPage(int idx) {
        if (idx >= 0 && idx < getTabCount()) {
            SeerBoldTitlesTabbedPaneHeader header = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(idx);
            if (header.isHeaderEnabled()) {
                setSelectedIndex(idx);
                _currentTabIndex = idx;
            }
        }
    }

    public void disablePage(String title) {
        SeerBoldTitlesTabbedPaneHeader header = _headers.get(title);
        if (header != null)
            header.disableHeader();
        Integer idx = _tabIndexes.get(title);
        if (idx != null && idx >= 0 && idx < getTabCount())
            setEnabledAt(idx, false);
    }

    public void disablePage(int idx) {
        SeerBoldTitlesTabbedPaneHeader header = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(idx);
        if (header != null)
            header.disableHeader();
        if (idx >= 0 && idx < getTabCount())
            setEnabledAt(idx, false);
    }

    public void disableAllPages() {
        for (String title : _headers.keySet())
            disablePage(title);
        this.setEnabled(false);
    }

    public void enablePage(String title) {
        SeerBoldTitlesTabbedPaneHeader header = _headers.get(title);
        if (header != null)
            header.enableHeader();
        Integer idx = _tabIndexes.get(title);
        if (idx != null && idx >= 0 && idx < getTabCount())
            setEnabledAt(idx, true);
    }

    public void enablePage(int idx) {
        SeerBoldTitlesTabbedPaneHeader header = (SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(idx);
        if (header != null)
            header.enableHeader();
        if (idx >= 0 && idx < getTabCount())
            setEnabledAt(idx, true);
    }

    public void enableAllPages() {
        this.setEnabled(true);
        for (String title : _headers.keySet())
            enablePage(title);
    }

    public Component getPage(String title) {
        return _tabs.get(title);
    }

    public Component getPage(int idx) {
        return getComponentAt(idx);
    }

    public int getPageIndex(String title) {
        return _tabIndexes.getOrDefault(title, -1);
    }

    public String getPageTitle(int idx) {
        for (Map.Entry<String, Integer> entry : _tabIndexes.entrySet())
            if (entry.getValue().equals(idx))
                return entry.getKey();
        return null;
    }

    public Component getCurrentPage() {
        String title = null;
        for (Map.Entry<String, Integer> entry : _tabIndexes.entrySet()) {
            if (entry.getValue() == _currentTabIndex) {
                title = entry.getKey();
                break;
            }
        }
        return getPage(title);
    }

    public int getCurrentPageIndex() {
        return _currentTabIndex;
    }

    public String getCurrentPageTitle() {
        for (Map.Entry<String, Integer> entry : _tabIndexes.entrySet())
            if (entry.getValue().equals(_currentTabIndex))
                return entry.getKey();
        return null;
    }

    public void applyExtraStateChangedLogic() {
    }

    public void updateHeader(int tabIdx, String text) {
        //get the previous title- the cached maps depend on this value
        String prevTitle = ((SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(tabIdx)).getHeaderTitle();

        //check if current title exists and if it matches the new title
        if (prevTitle == null || prevTitle.equals(text))
            return;

        //check if the title is already used on a different tab
        if (_tabs.containsKey(text))
            throw new RuntimeException("Duplicate titles are not allowed!");

        //update the map keys
        Component comp = _tabs.get(prevTitle);
        _tabs.put(text, comp);
        _tabs.remove(prevTitle);

        Integer idx = _tabIndexes.get(prevTitle);
        _tabIndexes.put(text, idx);
        _tabIndexes.remove(prevTitle);

        SeerBoldTitlesTabbedPaneHeader header = _headers.get(prevTitle);
        _headers.put(text, header);
        _headers.remove(prevTitle);

        //update the component text
        ((SeerBoldTitlesTabbedPaneHeader)getTabComponentAt(tabIdx)).updateHeader(text);
    }
}
