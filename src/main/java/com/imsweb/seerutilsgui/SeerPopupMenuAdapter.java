/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Use this class to register a <b>PopupMenuListener</b> with the <b>SeerMultiSelectComboBox</b> component,
 * without having to implement the three methods of the interface.
 */
public class SeerPopupMenuAdapter implements PopupMenuListener {

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // no default
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // no default
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // no default
    }
}
