/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

/**
 * This class allows a JEditorPane to highlight particular parts of its text.
 * <p/>
 * Created on Nov 17, 2010 by depryf
 * @author depryf
 */
@SuppressWarnings("unused")
public class SeerHighlightingEditorKit extends StyledEditorKit implements ViewFactory {

    /**
     * Highlighting information
     */
    protected transient List<List<Integer>> _highlighting;

    /**
     * Highlighting color
     */
    protected Color _highlightingColor;
    protected Color _foregroundColor;

    protected Font _font;

    /**
     * Constructor.
     * <p/>
     * Created on Nov 17, 2010 by depryf
     * @param highlighting a list of start/end positions that need to be highlighted (so sublists must all have 2 elements)
     */
    public SeerHighlightingEditorKit(List<List<Integer>> highlighting) {
        this(highlighting, Color.RED);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Nov 17, 2010 by depryf
     * @param highlighting a list of start/end positions that need to be highlighted (so sublists must all have 2 elements)
     * @param highlightingColor color for the highlighting
     */
    public SeerHighlightingEditorKit(List<List<Integer>> highlighting, Color highlightingColor) {
        this(highlighting, highlightingColor, Color.BLACK, null);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Nov 17, 2010 by depryf
     * @param highlighting a list of start/end positions that need to be highlighted (so sublists must all have 2 elements)
     * @param highlightingColor color for the highlighting
     * @param foregroundColor foreground color
     * @param font font to use
     */
    public SeerHighlightingEditorKit(List<List<Integer>> highlighting, Color highlightingColor, Color foregroundColor, Font font) {
        _highlighting = highlighting;
        _highlightingColor = highlightingColor;
        _foregroundColor = foregroundColor;
        _font = font;
    }

    public List<List<Integer>> getHighlighting() {
        return _highlighting;
    }

    @Override
    public View create(Element elem) {

        // passing true to this method will ensure the lines wrap at word boundaries instead of character boundaries
        return new WrappedPlainView(elem, true) {

            @Override
            protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
                // apply anti-aliasing fix
                ((Graphics2D)g).addRenderingHints(getHints());

                Document doc = getDocument();
                Segment segment = getLineBuffer();
                int ret = x;
                int currentPos = p0;

                // this is the tricky part; we have to split what needs to be drawn according to the requested highlighting (I am calling gaps the areas that need to be highlighted)
                int nextGapIndex = getNextGapIndex(p0, p1, 0);

                while (nextGapIndex != -1) {
                    List<Integer> nextGap = _highlighting.get(nextGapIndex);

                    // is there a non-gap (black) before the gap?
                    if (currentPos < nextGap.get(0)) {
                        g.setColor(_foregroundColor);
                        g.setFont(_font == null ? g.getFont().deriveFont(Font.PLAIN) : _font);
                        doc.getText(currentPos, nextGap.get(0) - currentPos, segment);
                        ret = Utilities.drawTabbedText(segment, ret, y, g, this, currentPos);
                        currentPos = nextGap.get(0);
                    }

                    // then draw the gap (but never go past p1)
                    int end = Math.min(p1, nextGap.get(1) + 1);
                    g.setColor(_highlightingColor);
                    g.setFont(_font == null ? g.getFont().deriveFont(Font.BOLD) : _font.deriveFont(Font.BOLD));
                    doc.getText(currentPos, end - currentPos, segment);
                    ret = Utilities.drawTabbedText(segment, ret, y, g, this, currentPos);
                    currentPos = end;

                    nextGapIndex = getNextGapIndex(currentPos, p1, nextGapIndex + 1);
                }

                // finish last non-gap in black (if any)
                if (currentPos < p1) {
                    g.setColor(_foregroundColor);
                    g.setFont(_font == null ? g.getFont().deriveFont(Font.PLAIN) : _font);
                    doc.getText(currentPos, p1 - currentPos, segment);
                    ret = Utilities.drawTabbedText(segment, ret, y, g, this, currentPos);
                }

                return ret;
            }

            private int getNextGapIndex(int start, int end, int indexOffset) {
                int result = -1;

                if (_highlighting == null || indexOffset == _highlighting.size())
                    return result;

                for (int i = indexOffset; i < _highlighting.size(); i++) {
                    List<Integer> gap = _highlighting.get(i);
                    int gapStart = gap.get(0);
                    int gapEnd = gap.get(1);
                    if ((gapStart >= start && gapStart <= end) || (gapEnd >= start && gapEnd <= end)) {
                        result = i;
                        break;
                    }
                }

                return result;
            }

            /**
             * no sure why I need to do this trick, but if I don't Swing doesn't render the anti-aliasing and the text looks bad; I found the fix here:
             *     <a href="http://netbeans.sourcearchive.com/documentation/6.1-0ubuntu1/ExtPlainView_8java-source.html">link</a>)
             */
            @SuppressWarnings("unchecked")
            private Map<Object, Object> getHints() {
                Map<Object, Object> hints = (Map<Object, Object>)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints")); //NOI18N
                if (hints == null) {
                    hints = new HashMap<>();
                    if (Boolean.getBoolean("swing.aatext") || "Aqua".equals(UIManager.getLookAndFeel().getID()))
                        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }
                return hints;
            }
        };
    }

    @Override
    public ViewFactory getViewFactory() {
        return this;
    }
}
