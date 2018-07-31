/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License 
 *       at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 */
package com.imsweb.seerutilsgui.editor;

import java.io.Serializable;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class LexerToken implements Serializable, Comparable<LexerToken> {

    /** Available token type */
    public static enum TokenType {
        OPERATOR, // Language operators
        KEYWORD, // language reserved keywords
        KEYWORD2, // Other language reserved keywords, like C #defines
        IDENTIFIER, // identifiers, variable names, class names
        NUMBER, // numbers in various formats
        STRING, // String
        STRING2, // For highlighting meta chars within a String
        COMMENT, // comments
        COMMENT2, // special stuff within comments
        REGEX, // regular expressions
        REGEX2, // special chars within regular expressions
        TYPE, // Types, usually not keywords, but supported by the language
        TYPE2, // Types from standard libraries
        TYPE3, // Types for users
        DEFAULT, // any other text
        WARNING, // Text that should be highlighted as a warning
        ERROR, // Text that signals an error
        CDATA, // CDATA tags
        CDATA_CONTENT
        // CDATA content
    }

    /** Token type */
    public final TokenType type;

    /** Token length */
    public final int length;

    /** Token start in document text */
    public final int start;

    /**
     * the pair value to use if this token is one of a pair:
     * This is how it is used:
     * The openning part will have a positive number X
     * The closing part will have a negative number X
     * X should be unique for a pair:
     * e.g. for [ pairValue = +1
     * for ] pairValue = -1
     */
    public final byte pairValue;

    /**
     * Constructs a new token
     * @param t type
     * @param s start
     * @param l length
     */
    public LexerToken(TokenType t, int s, int l) {
        this.type = t;
        this.start = s;
        this.length = l;
        this.pairValue = 0;
    }

    /**
     * Construct a new part of pair token
     * @param t type
     * @param s start
     * @param l length
     * @param p pairValue
     */
    public LexerToken(TokenType t, int s, int l, byte p) {
        this.type = t;
        this.start = s;
        this.length = l;
        this.pairValue = p;
    }

    /* (non-Javadoc)
     * 
     * Created on Nov 19, 2008 by depryf
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LexerToken))
            return false;
        LexerToken token = (LexerToken)obj;
        return ((this.start == token.start) && (this.length == token.length) && (this.type.equals(token.type)));
    }

    /* (non-Javadoc)
     * 
     * Created on Nov 19, 2008 by depryf
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return start;
    }

    /* (non-Javadoc)
     * 
     * Created on Nov 19, 2008 by depryf
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s (%d, %d) (%d)", type, start, length, pairValue);
    }

    /* (non-Javadoc)
     * 
     * Created on Nov 19, 2008 by depryf
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(LexerToken o) {
        if (this.start != o.start)
            return (this.start - o.start);

        if (this.length != o.length)
            return (this.length - o.length);

        return this.type.compareTo(o.type);
    }

    /**
     * return the end position of the token.
     * @return start + length
     */
    public int end() {
        return start + length;
    }

    /**
     * Get the text of the token from this document
     * @param doc document
     * @return the content of the document
     */
    public String getText(Document doc) {
        String text;

        try {
            text = doc.getText(start, length);
        }
        catch (BadLocationException ex) {
            throw new RuntimeException("Unable to get text for given token", ex);
        }

        return text;
    }
}
