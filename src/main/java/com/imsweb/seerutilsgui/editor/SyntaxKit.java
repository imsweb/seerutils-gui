/*
 * Copyright (C) 2008 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

@SuppressWarnings("unused")
public class SyntaxKit extends DefaultEditorKit implements ViewFactory {

    /**
     * Available syntax kit
     */
    public static final String SYNTAX_TYPE_PLAIN = "plain";
    public static final String SYNTAX_TYPE_PROPERTIES = "properties";
    public static final String SYNTAX_TYPE_XML = "xml";
    public static final String SYNTAX_TYPE_GROOVY = "groovy";
    public static final String SYNTAX_TYPE_SQL = "sql";

    /**
     * Current style for this editor
     */
    private final transient SyntaxStyles _style;

    /**
     * Lexer (depends on the type of editor)
     */
    private final transient Lexer _lexer;

    /**
     * Constructor.
     * <p/>
     * Created on Apr 24, 2012 by depryf
     * @param type kit type
     */
    public SyntaxKit(String type) {
        this(type, null);
    }

    /**
     * Constructor
     * <p/>
     * Created on Apr 24, 2012 by depryf
     * @param type kit type
     */
    public SyntaxKit(String type, Properties syntaxProperties) {
        super();

        // type
        if (SYNTAX_TYPE_PLAIN.equals(type))
            _lexer = null;
        else if (SYNTAX_TYPE_PROPERTIES.equals(type))
            _lexer = new LexerProperties();
        else if (SYNTAX_TYPE_XML.equals(type))
            _lexer = new LexerXml();
        else if (SYNTAX_TYPE_GROOVY.equals(type))
            _lexer = new LexerGroovy();
        else if (SYNTAX_TYPE_SQL.equals(type))
            _lexer = new LexerSql();
        else
            throw new RuntimeException("Unsupported type: " + type);

        // style
        _style = new SyntaxStyles(syntaxProperties);
    }

    public void disable() {
        _style.useDisabled(true);
    }

    public void enable() {
        _style.useDisabled(false);
    }

    @Override
    public ViewFactory getViewFactory() {
        return this;
    }

    @Override
    public View create(Element element) {
        return new PlainView(element) {
            @Override
            protected int drawUnselectedText(Graphics graphics, int x, int y, int p0, int p1) {
                Font saveFont = graphics.getFont();
                Color saveColor = graphics.getColor();
                SyntaxDocument doc = (SyntaxDocument)getDocument();
                Segment segment = getLineBuffer();

                try {
                    // Colour the parts
                    Iterator<LexerToken> i = doc.getTokens(p0, p1);
                    int start = p0;
                    while (i.hasNext()) {
                        LexerToken t = i.next();
                        // if there is a gap between the next token start and where we
                        // should be starting (spaces not returned in tokens), then draw
                        // it in the default type
                        if (start < t.start) {
                            int length = t.start - start;
                            doc.getText(start, length, segment);
                            x = _style.drawText(segment, x, y, graphics, this, new LexerToken(LexerToken.TokenType.DEFAULT, start, length));
                        }
                        // t and s are the actual start and length of what we should
                        // put on the screen.  assume these are the whole token....
                        int l = t.length;
                        int s = t.start;
                        // ... unless the token starts before p0:
                        if (s < p0) {
                            // token is before what is requested. adgust the length and s
                            l -= (p0 - s);
                            s = p0;
                        }
                        // if token end (s + l is still the token end pos) is greater 
                        // than p1, then just put up to p1
                        if (s + l > p1) {
                            l = p1 - s;
                        }
                        doc.getText(s, l, segment);
                        x = _style.drawText(segment, x, y, graphics, this, t);
                        start = t.end();
                    }
                    // now for any remaining text not tokenized:
                    if (start < p1) {
                        int length = p1 - start;
                        doc.getText(start, length, segment);
                        x = _style.drawText(segment, x, y, graphics, this, new LexerToken(LexerToken.TokenType.DEFAULT, start, length));
                    }
                }
                catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                finally {
                    graphics.setFont(saveFont);
                    graphics.setColor(saveColor);
                }
                return x;
            }
        };
    }

    @Override
    public Document createDefaultDocument() {
        return new SyntaxDocument(_lexer);
    }
}
