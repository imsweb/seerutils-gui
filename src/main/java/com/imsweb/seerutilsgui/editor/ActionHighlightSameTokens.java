/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

public class ActionHighlightSameTokens extends TextAction implements CaretListener {

    private Set<LexerToken.TokenType> _tokenTypes = new HashSet<>();

    private SyntaxUtils.SimpleMarker _marker;

    public ActionHighlightSameTokens() {
        super("HIGHTLIGHT_SAME_TOKENS");

        _tokenTypes.add(LexerToken.TokenType.IDENTIFIER);
        _tokenTypes.add(LexerToken.TokenType.TYPE);
        _tokenTypes.add(LexerToken.TokenType.TYPE2);
        _tokenTypes.add(LexerToken.TokenType.TYPE3);

        _marker = new SyntaxUtils.SimpleMarker(Color.LIGHT_GRAY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        SyntaxDocument sDoc = SyntaxUtils.getSyntaxDocument(target);
        if (target != null && sDoc != null) {
            LexerToken token = sDoc.getTokenAt(target.getCaretPosition());
            if (token != null && _tokenTypes.contains(token.type)) {
                removeMarkers(target);
                addMarkers(target, sDoc, token);
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        removeMarkers((JTextComponent)e.getSource());
    }

    /**
     * removes all markers from the pane.
     */
    private void removeMarkers(JTextComponent comp) {
        SyntaxUtils.removeMarkers(comp, _marker);
    }

    /**
     * add highlights for the given pattern
     * @param comp text component
     * @param sDoc syntax document
     * @param token token
     */
    private void addMarkers(JTextComponent comp, SyntaxDocument sDoc, LexerToken token) {
        sDoc.readLock();
        String text = token.getText(sDoc);
        Iterator<LexerToken> it = sDoc.getTokens(0, sDoc.getLength());
        while (it.hasNext()) {
            LexerToken nextToken = it.next();
            if (nextToken.length == token.length && text.equals(nextToken.getText(sDoc)))
                SyntaxUtils.markToken(comp, nextToken, _marker);
        }
        sDoc.readUnlock();
    }
}
