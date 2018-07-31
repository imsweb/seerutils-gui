/*
 * Copyright (C) 2009 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.table;

public interface SeerCellListener {

    /**
     * Called when an action is performed on a cell (button clicked, checkbox toggled, etc...)
     * <p/>
     * Created on Mar 31, 2009 by depryf
     * @param e cell event
     */
    void actionPerformed(SeerCellEvent e);
}
