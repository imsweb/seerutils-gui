/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.table;

import java.io.Serializable;

import com.imsweb.seerutilsgui.SeerGuiUtils;

@SuppressWarnings("unused")
public class SeerColumn implements Serializable {

    public enum SeerColumnWidthType {
        MIN, MAX, FIXED
    }

    public enum SeerColumnSortOrderType {
        ASCENDING, DESCENDING
    }

    private String _header;
    private Class<?> _contentType;
    private Boolean _editable;
    private Boolean _centerContent;
    private Boolean _boldContent;
    private Boolean _visible;
    private String _defaultValue;
    private Integer _fixedSize;
    private SeerColumnWidthType _width;
    private Boolean _longText;
    private String _lookup;
    private SeerColumnSortOrderType _defaultSort;
    private String _tooltip;

    public SeerColumn(String header) {
        _header = header;
        _contentType = String.class;
        _centerContent = Boolean.FALSE;
        _boldContent = Boolean.FALSE;
        _visible = Boolean.TRUE;
        _defaultValue = null;
        _fixedSize = null;
        _width = SeerColumnWidthType.MAX;
        _editable = Boolean.FALSE;
        _longText = Boolean.FALSE;
        _lookup = null;
    }

    public String getHeader() {
        return _header;
    }

    public SeerColumn setHeader(String header) {
        _header = header;
        return this;
    }

    public Class<?> getContentType() {
        return _contentType;
    }

    public SeerColumn setContentType(Class<?> type) {
        _contentType = type;
        return this;
    }

    public Boolean getCenterContent() {
        return _centerContent;
    }

    public SeerColumn setCenterContent(Boolean center) {
        _centerContent = center;
        return this;
    }

    public Boolean getBoldContent() {
        return _boldContent;
    }

    public SeerColumn setBoldContent(Boolean bold) {
        _boldContent = bold;
        return this;
    }

    public Boolean getVisible() {
        return _visible;
    }

    public SeerColumn setVisible(Boolean visible) {
        _visible = visible;
        return this;
    }

    public String getDefaultValue() {
        return _defaultValue;
    }

    public SeerColumn setDefaultValue(String value) {
        _defaultValue = value;
        return this;
    }

    public Integer getFixedSize() {
        return _fixedSize;
    }

    public SeerColumn setFixedSize(Integer size) {
        return setFixedSize(size, true);
    }

    public SeerColumn setFixedSize(Integer size, boolean adjustForFontSize) {
        _fixedSize = size;
        if (SeerGuiUtils.getFontDelta() > 0 && adjustForFontSize)
            _fixedSize = _fixedSize + (_fixedSize * SeerGuiUtils.getFontDelta() / 12);
        return this;
    }

    public SeerColumnWidthType getWidth() {
        return _width;
    }

    public SeerColumn setWidth(SeerColumnWidthType width) {
        _width = width;
        return this;
    }

    public Boolean getEditable() {
        return _editable;
    }

    public SeerColumn setEditable(Boolean editable) {
        _editable = editable;
        return this;
    }

    public Boolean getLongText() {
        return _longText;
    }

    public SeerColumn setLongText(Boolean longText) {
        _longText = longText;
        return this;
    }

    public String getLookup() {
        return _lookup;
    }

    public SeerColumn setLookup(String lookup) {
        _lookup = lookup;
        return this;
    }

    public SeerColumnSortOrderType getDefaultSort() {
        return _defaultSort;
    }

    public SeerColumn setDefaultSort(SeerColumnSortOrderType sort) {
        _defaultSort = sort;
        return this;
    }

    public String getTooltip() {
        return _tooltip;
    }

    public SeerColumn setTooltip(String tooltip) {
        _tooltip = tooltip;
        return this;
    }
}
