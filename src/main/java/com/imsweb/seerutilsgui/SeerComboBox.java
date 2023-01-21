package com.imsweb.seerutilsgui;/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * This class extends JComboBox and adds functionality for showing separators.
 * <br/><br/>
 * If a user selects a separator, the selected value won't change, but a selection change
 * event will still be triggered; calling getSelectedValue() on that event will return null.
 */
@SuppressWarnings("unused")
public class SeerComboBox<E> extends JComboBox<E> {

    private transient Object _currentValue;

    private transient ActionListener _listener;

    private final String _separatorStart;

    public SeerComboBox(List<E> elements) {
        this(elements, "===");
    }

    public SeerComboBox(List<E> elements, String separatorStart) {
        super(new Vector<>(elements));

        _separatorStart = separatorStart;

        this.setFont(SeerGuiUtils.adjustFontSize(this.getFont()));
        this.setRenderer(new SeerComboBoxRenderer());

        _currentValue = getSelectedItem();
        this.addActionListener(e -> {
            Object val = getSelectedItem();
            if (val != null && !val.equals(_currentValue) && !Objects.toString(val).trim().startsWith(_separatorStart)) {
                _currentValue = val;
                if (_listener != null)
                    _listener.actionPerformed(e);
            }
            else
                setSelectedItem(_currentValue);
        });
    }

    @SuppressWarnings("unchecked")
    public E getSelectedValue() {
        E val = (E)this.getSelectedItem();
        return Objects.toString(val, "").trim().startsWith(_separatorStart) ? null : val;
    }

    public void setActionListener(ActionListener l) {
        _listener = l;
    }

    private class SeerComboBoxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (Objects.toString(value, "").trim().startsWith(_separatorStart)) {
                lbl.setForeground(Color.GRAY);
                lbl.setBorder(BorderFactory.createEmptyBorder());
            }
            else
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            return lbl;
        }
    }
}
