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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * This class contains static utility methods to make highlighting in text components easier.
 */
@SuppressWarnings("unused")
public class SyntaxUtils {

    private SyntaxUtils() {
        // utility class only...
    }

    // This subclass is used in our highlighting code
    public static class SimpleMarker extends DefaultHighlighter.DefaultHighlightPainter {

        public SimpleMarker(Color color) {
            super(color);
        }
    }

    /**
     * Removes only our private highlights
     * This is public so that we can remove the highlights when the editorKit
     * is unregistered.  SimpleMarker can be null, in which case all instances of
     * our Markers are removed.
     */
    public static void removeMarkers(JTextComponent component, SimpleMarker marker) {
        Highlighter hihglighter = component.getHighlighter();

        for (Highlighter.Highlight h : hihglighter.getHighlights()) {
            if (h.getPainter() instanceof SimpleMarker) {
                SimpleMarker hMarker = (SimpleMarker)h.getPainter();
                if (marker == null || hMarker.equals(marker))
                    hihglighter.removeHighlight(h);
            }
        }
    }

    /**
     * Remove all the markers from an editorpane
     * @param editorPane text component
     */
    public static void removeMarkers(JTextComponent editorPane) {
        removeMarkers(editorPane, null);
    }

    /**
     * add highlights for the given Token on the given pane
     * @param pane text component
     * @param token token
     * @param marker marker
     */
    public static void markToken(JTextComponent pane, LexerToken token, SimpleMarker marker) {
        markText(pane, token.start, token.end(), marker);
    }

    /**
     * add highlights for the given region on the given pane
     * @param pane text component
     * @param start start
     * @param end end
     * @param marker marker
     */
    public static void markText(JTextComponent pane, int start, int end, SimpleMarker marker) {
        try {
            Highlighter hiliter = pane.getHighlighter();

            int selStart = pane.getSelectionStart();
            int selEnd = pane.getSelectionEnd();

            // if there is no selection or selection does not overlap
            if (selStart == selEnd || end < selStart || start > selStart) {
                hiliter.addHighlight(start, end, marker);
                return;
            }

            // selection starts within the highlight, highlight before slection
            if (selStart > start && selStart < end)
                hiliter.addHighlight(start, selStart, marker);

            // selection ends within the highlight, highlight remaining
            if (selEnd > start && selEnd < end)
                hiliter.addHighlight(selEnd, end, marker);
        }
        catch (BadLocationException ex) {
            throw new RuntimeException("Unable to highlight text", ex);
        }
    }

    /**
     * Mark all text in the document that matches the given pattern
     * @param pane control to use
     * @param pattern pattern to match
     * @param marker marker to use for highlighting
     */
    public static void markAll(JTextComponent pane, Pattern pattern, SimpleMarker marker) {
        SyntaxDocument sDoc = getSyntaxDocument(pane);
        if (sDoc == null || pattern == null)
            return;

        Matcher matcher = sDoc.getMatcher(pattern);
        while (matcher.find())
            markText(pane, matcher.start(), matcher.end(), marker);
    }

    /**
     * A helper function that will return the SyntaxDocument attached to the
     * given text component.  Return null if the document is not a
     * SyntaxDocument, or if the text component is null
     * @param component text component
     * @return corresponding syntax document, null if not available
     */
    public static SyntaxDocument getSyntaxDocument(JTextComponent component) {
        if (component == null)
            return null;

        Document doc = component.getDocument();
        if (doc instanceof SyntaxDocument)
            return (SyntaxDocument)doc;

        return null;
    }

    /**
     * Return the line of text at the given position.  The returned value may
     * be null.  It will not contain the trailing new-line character.
     * @param component text component
     * @param pos cursor position
     * @return correspoding line
     */
    public static String getLineAt(JTextComponent component, int pos) {
        String line = null;

        Document doc = component.getDocument();
        if (doc instanceof PlainDocument) {
            PlainDocument pDoc = (PlainDocument)doc;
            int start = pDoc.getParagraphElement(pos).getStartOffset();
            int end = pDoc.getParagraphElement(pos).getEndOffset();
            try {
                line = doc.getText(start, end - start);
                if (line != null && line.endsWith("\n")) {
                    line = line.substring(0, line.length() - 1);
                }
            }
            catch (BadLocationException ex) {
                throw new RuntimeException("Unable to get current line", ex);
            }
        }

        return line;
    }

    /**
     * Gets the Line Number at the give position of the editor component.
     * The first line number is ZERO
     * @param component text component
     * @param pos cursor position
     * @return line number
     */
    public static int getLineNumber(JTextComponent component, int pos) {
        try {
            Rectangle r = component.modelToView(pos);
            if (r == null)
                return 0;
            return r.y / component.getFontMetrics(component.getFont()).getHeight();
        }
        catch (BadLocationException ex) {
            throw new RuntimeException("Unable to get current line", ex);
        }
    }

    /**
     * Gets the column number at given position of editor.  The first column is ZERO
     * @param component text component
     * @param pos cursor position
     * @return column number
     */
    public static int getColumnNumber(JTextComponent component, int pos) {
        try {
            // this will be fixed when the project stops supporting Java 8...
            Rectangle r = component.modelToView(pos);
            if (r == null)
                return 0;
            // this will be fixed when the project stops supporting Java 8...
            return pos - component.viewToModel(new Point(0, r.y));
        }
        catch (BadLocationException ex) {
            throw new RuntimeException("Unable to get current line", ex);
        }
    }

    /**
     * Gets the number of line in the provided component
     * @param component text component
     * @return line counts
     */
    public static int getLineCount(JTextComponent component) {
        int count = 0;

        int p = component.getDocument().getLength() - 1;
        if (p > 0)
            count = getLineNumber(component, p);

        return count;
    }
}
