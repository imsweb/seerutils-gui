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

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import javax.swing.undo.UndoManager;

/**
 * A document that supports being highlighted.  The document maintains an internal List of all the Tokens.
 * The Tokens are updated using a Lexer, passed to it during construction.
 */
@SuppressWarnings("unused")
public class SyntaxDocument extends PlainDocument {

    /**
     * Private <code>Lexer</code> used to parse the content of this document
     */
    private final transient Lexer _lexer;

    /**
     * List of the <code>Token</code> contained in this document
     */
    private List<LexerToken> _tokens;

    /**
     * Undo manager
     */
    private final UndoManager _undoMgr = new UndoManager();

    /**
     * Action that needs to be executed when the content of the document changes
     */
    private AbstractAction _keyAction;

    /**
     * Constructor.
     */
    public SyntaxDocument(Lexer lexer) {
        super();

        this._lexer = lexer;

        addUndoableEditListener(evt -> {
            if (evt.getEdit().isSignificant()) {
                _undoMgr.addEdit(evt.getEdit());
            }
        });
    }

    /**
     * Parse the entire document and return list of tokens that do not already
     * exist in the tokens list.  There may be overlaps, and replacements,
     * which we will cleanup later.
     */
    @SuppressWarnings("java:S2093") // try-with-resources
    private void parse() {

        // if we have no lexer, then we must have no tokens...
        if (_lexer == null) {
            _tokens = null;
            return;
        }

        List<LexerToken> toks = new ArrayList<>(getLength() / 10);
        try {
            Segment seg = new Segment();
            getText(0, getLength(), seg);
            CharArrayReader reader = new CharArrayReader(seg.array, seg.offset, seg.count);
            _lexer.yyreset(reader);
            LexerToken token;
            while ((token = _lexer.yylex()) != null)
                toks.add(token);
        }
        catch (BadLocationException | IOException ex) {
            throw new RuntimeException("unable to parse document", ex);
        }
        finally {
            _tokens = toks;
        }
    }

    public void setKeyAction(AbstractAction e) {
        _keyAction = e;
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);

        if (_keyAction != null)
            _keyAction.actionPerformed(null);
    }

    @Override
    protected void removeUpdate(DefaultDocumentEvent chng) {
        super.removeUpdate(chng);

        if (_keyAction != null)
            _keyAction.actionPerformed(null);
    }

    @Override
    protected void fireChangedUpdate(DocumentEvent e) {
        parse();
        super.fireChangedUpdate(e);
    }

    @Override
    protected void fireInsertUpdate(DocumentEvent e) {
        parse();
        super.fireInsertUpdate(e);
    }

    @Override
    protected void fireRemoveUpdate(DocumentEvent e) {
        parse();
        super.fireRemoveUpdate(e);
    }

    /**
     * Replace the token with the replacement string
     * @param token token
     * @param replacement string to replace
     */
    public void replaceToken(LexerToken token, String replacement) {
        try {
            replace(token.start, token.length, replacement, null);
        }
        catch (BadLocationException ex) {
            throw new RuntimeException("unable to replace token: " + token, ex);
        }
    }

    /**
     * This class is used to iterate over tokens between two positions
     */
    final class TokenIterator implements ListIterator<LexerToken> {

        int _start;
        int _end;
        int _ndx = 0;

        private TokenIterator(int start, int end) {
            this._start = start;
            this._end = end;
            if (_tokens != null && !_tokens.isEmpty()) {
                LexerToken token = new LexerToken(LexerToken.TokenType.COMMENT, start, end - start);
                _ndx = Collections.binarySearch(_tokens, token);
                // we will probably not find the exact token...
                if (_ndx < 0) {
                    // so, start from one before the token where we should be...
                    // -1 to get the location, and another -1 to go back..
                    _ndx = Math.max(-_ndx - 1 - 1, 0);
                    LexerToken t = _tokens.get(_ndx);
                    // if the prev token does not overlap, then advance one
                    if (t.end() <= start) {
                        _ndx++;
                    }

                }
            }
        }

        @Override
        public boolean hasNext() {
            if (_tokens == null) {
                return false;
            }
            if (_ndx >= _tokens.size()) {
                return false;
            }
            LexerToken t = _tokens.get(_ndx);
            return t.start < _end;
        }

        @Override
        @SuppressWarnings("java:S2272") // deal with end of iteration
        public LexerToken next() {
            return _tokens.get(_ndx++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasPrevious() {
            if (_tokens == null) {
                return false;
            }
            if (_ndx <= 0) {
                return false;
            }
            LexerToken t = _tokens.get(_ndx);
            return t.end() > _start;
        }

        @Override
        public LexerToken previous() {
            return _tokens.get(_ndx--);
        }

        @Override
        public int nextIndex() {
            return _ndx + 1;
        }

        @Override
        public int previousIndex() {
            return _ndx - 1;
        }

        @Override
        public void set(LexerToken e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(LexerToken e) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Return an iterator of tokens between p0 and p1.
     * @param start start position for getting tokens
     * @param end position for last token
     * @return Iterator for tokens that overal with range from start to end
     */
    public Iterator<LexerToken> getTokens(int start, int end) {
        return new TokenIterator(start, end);
    }

    /**
     * Find the token at a given position.  May return null if no token is
     * found (whitespace skipped) or if the position is out of range:
     * @param pos requested position
     * @return corresponding token, null if not available
     */
    public LexerToken getTokenAt(int pos) {
        if (_tokens == null || _tokens.isEmpty() || pos > getLength())
            return null;

        LexerToken tok = null;
        LexerToken tKey = new LexerToken(LexerToken.TokenType.DEFAULT, pos, 1);
        int ndx = Collections.binarySearch(_tokens, tKey);
        if (ndx < 0) {
            // so, start from one before the token where we should be...
            // -1 to get the location, and another -1 to go back..
            ndx = Math.max(-ndx - 1 - 1, 0);
            LexerToken t = _tokens.get(ndx);
            if ((t.start <= pos) && (pos <= t.end())) {
                tok = t;
            }
        }
        else {
            tok = _tokens.get(ndx);
        }
        return tok;
    }

    /**
     * This is used to return the other part of a paired token in the document.
     * A paired part has token.pairValue <> 0, and the paired token will
     * have the negative of t.pairValue.
     * This method properly handles nestings of same pairValues, but overlaps
     * are not checked.
     * if The document does not contain a paired
     * @param t token
     * @return the other pair's token, or null if nothing is found.
     */
    public LexerToken getPairFor(LexerToken t) {
        if (_tokens == null || t == null || t.pairValue == 0) {
            return null;
        }
        LexerToken p = null;
        int ndx = _tokens.indexOf(t);
        // w will be similar to a stack. The openers weight is added to it
        // and the closers are subtracted from it (closers are already negative)
        int w = t.pairValue;
        int direction = (t.pairValue > 0) ? 1 : -1;
        boolean done = false;
        int v = Math.abs(t.pairValue);
        while (!done) {
            ndx += direction;
            if (ndx < 0 || ndx >= _tokens.size()) {
                break;
            }
            LexerToken current = _tokens.get(ndx);
            if (Math.abs(current.pairValue) == v) {
                w += current.pairValue;
                if (w == 0) {
                    p = current;
                    done = true;
                }
            }
        }

        return p;
    }

    /**
     * Return a matcher that matches the given pattern on the entire document
     * @param pattern pattern
     * @return matcher object
     */
    public Matcher getMatcher(Pattern pattern) {
        return getMatcher(pattern, 0, getLength());
    }

    /**
     * Return a matcher that matches the given pattern in the part of the
     * document starting at offset start.  Note that the matcher will have
     * offset starting from <code>start</code>
     * @param pattern pattern
     * @param start start position
     * @return matcher that <b>MUST</b> be offset by start to get the proper
     * location within the document
     */
    public Matcher getMatcher(Pattern pattern, int start) {
        return getMatcher(pattern, start, getLength() - start);
    }

    /**
     * Return a matcher that matches the given pattern in the part of the
     * document starting at offset start and ending at start + length.
     * Note that the matcher will have
     * offset starting from <code>start</code>
     * @param pattern pattern
     * @param start start position
     * @return matcher that <b>MUST</b> be offset by start to get the proper
     * location within the document
     */
    public Matcher getMatcher(Pattern pattern, int start, int length) {
        Matcher matcher;
        if (getLength() == 0)
            return null;

        try {
            Segment seg = new Segment();
            getText(start, length, seg);
            matcher = pattern.matcher(seg);
        }
        catch (BadLocationException ex) {
            throw new RuntimeException("Requested offset: " + ex.offsetRequested(), ex);
        }

        return matcher;
    }

    /**
     * Gets the line at given position.  The line returned will NOT include
     * the line terminator '\n'
     * @param pos Position (usually from text.getCaretPosition()
     * @return the String of text at given position
     */
    public String getLineAt(int pos) throws BadLocationException {
        Element e = getParagraphElement(pos);
        Segment seg = new Segment();
        getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset(), seg);
        char last = seg.last();
        if (last == '\n' || last == '\r')
            return seg.subSequence(0, seg.length() - 1).toString();

        return seg.toString();
    }

    /**
     * Returns the number of lines.
     * <p/>
     * Created on Jun 29, 2011 by Fabian
     * @return number of lines for this document
     */
    public int getNumberOfLines() {
        return getDefaultRootElement().getElementCount();
    }

    /**
     * Returns the start line offset of teh requested line number.
     * <p/>
     * Created on Jun 29, 2011 by Fabian
     * @param lineNumber line number
     * @return start of line
     */
    public int getStartOfLineFromLineNumber(int lineNumber) {
        return getDefaultRootElement().getElement(lineNumber).getStartOffset();
    }

    /**
     * Deletes the line at given position
     * @param pos position
     */
    public void removeLineAt(int pos) throws BadLocationException {
        Element e = getParagraphElement(pos);
        remove(e.getStartOffset(), getElementLength(e));
    }

    /**
     * Replace the line at given position with the given string, which can span multiple lines
     * @param pos position
     * @param newLines lines to replace
     */
    public void replaceLineAt(int pos, String newLines) throws BadLocationException {
        Element e = getParagraphElement(pos);
        replace(e.getStartOffset(), getElementLength(e), newLines, null);
    }

    /**
     * Helper method to get the length of an element and avoid getting
     * a too long element at the end of the document
     * @param e element
     * @return lenght of the element
     */
    private int getElementLength(Element e) {
        int end = e.getEndOffset();
        if (end >= (getLength() - 1))
            end--;

        return end - e.getStartOffset();
    }

    /**
     * Perform an undo action, if possible
     */
    public void doUndo() {
        if (_undoMgr.canUndo())
            _undoMgr.undo();
    }

    /**
     * Perform a redo action, if possible.
     */
    public void doRedo() {
        if (_undoMgr.canRedo())
            _undoMgr.redo();
    }

    public void resetUndoMgr() {
        _undoMgr.discardAllEdits();
    }
}
