/*
 * Copyright (C) 2009 Information Management Services, Inc.
 */
package com.imsweb.shared.gui.table;

import java.awt.event.ActionEvent;

public class SeerCellEvent extends ActionEvent {

    private int _row, _col;

    public SeerCellEvent(Object source, int id, String command, int row, int col) {
        super(source, id, command);

        _row = row;
        _col = col;
    }

    public int getRow() {
        return _row;
    }

    public int getCol() {
        return _col;
    }
}
