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
package com.imsweb.shared.gui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;

/**
 * The Styles to use for each TokenType.  The defaults are created here, and
 * then the resource META-INF/services/syntaxstyles.properties is read and
 * merged.  You can also pass a properties instance and merge your prefered
 * styles into the default styles
 */
public final class SyntaxStyles {

    /**
     * Current styles
     */
    private Map<LexerToken.TokenType, SyntaxStyle> _styles = new HashMap<>();

    /**
     * Default style
     */
    private SyntaxStyle _defaultStyle = new SyntaxStyle(Color.BLACK, Font.PLAIN);

    /**
     * Disabled style
     */
    private SyntaxStyle _disabledStyle = new SyntaxStyle(Color.GRAY, Font.PLAIN);

    /**
     * Whether the disabled style should be use or not
     */
    private boolean _useDisabled = false;

    /**
     * Default constructor.
     * <p/>
     * Created on Apr 24, 2012 by depryf
     */
    public SyntaxStyles() {
        this(null);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Apr 24, 2012 by depryf
     * @param styles styles as properties
     */
    public SyntaxStyles(Properties styles) {
        if (styles != null) {
            for (String token : styles.stringPropertyNames()) {
                String stv = styles.getProperty(token);
                try {
                    LexerToken.TokenType tt = LexerToken.TokenType.valueOf(token);
                    String[] parts = stv.split("\\s*,\\s*");
                    if (parts.length != 2)
                        throw new IllegalArgumentException("style not correct format: " + stv);

                    _styles.put(tt, new SyntaxStyle(new Color(Integer.decode(parts[0])), Integer.decode(parts[1])));
                }
                catch (IllegalArgumentException ex) {
                    throw new RuntimeException("illegal token type or style for: " + token);
                }
            }
        }
    }

    public void useDisabled(boolean disabled) {
        _useDisabled = disabled;
    }

    /**
     * Draw the given Token.  This will simply find the proper SyntaxStyle for the TokenType and then asks the proper Style to draw the text of the Token.
     * <p/>
     * @param segment the source of the text
     * @param x the X origin >= 0
     * @param y the Y origin >= 0
     * @param graphics the graphics context
     * @param e how to expand the tabs. If this value is null, tabs will be expanded as a space character.
     * @param token token
     * @return the X location at the end of the rendered text
     */
    public int drawText(Segment segment, int x, int y, Graphics graphics, TabExpander e, LexerToken token) {
        if (token == null)
            throw new RuntimeException("Wasn't expecting a null token but got it");

        SyntaxStyle style;
        if (_useDisabled)
            style = _disabledStyle;
        else
            style = _styles.get(token.type);

        if (style == null)
            style = _defaultStyle;

        return style.drawText(segment, x, y, graphics, e, token.start);
    }

    private static final class SyntaxStyle {

        /**
         * Color for this style
         */
        private Color _color;

        /**
         * Font properties for this style
         */
        private int _fontStyle;

        /**
         * Constructor.
         * <p/>
         * Created on Apr 24, 2012 by depryf
         * @param color color
         * @param fontStyle font style
         */
        public SyntaxStyle(Color color, int fontStyle) {
            _color = color;
            _fontStyle = fontStyle;
        }

        /**
         * Getter.
         * <p/>
         * Created on Apr 24, 2012 by depryf
         * @return color
         */
        public Color getColor() {
            return _color;
        }

        /**
         * Getter.
         * <p/>
         * Created on Apr 24, 2012 by depryf
         * @return font style
         */
        public int getFontStyle() {
            return _fontStyle;
        }

        /**
         * Draw text.  This can directly call the Utilities.drawTabbedText. Sub-classes can override this method to provide any other decorations.
         * <p/>
         * @param segment the source of the text
         * @param x the X origin >= 0
         * @param y the Y origin >= 0
         * @param graphics the graphics context
         * @param e how to expand the tabs. If this value is null, tabs will be expanded as a space character.
         * @param startOffset starting offset of the text in the document >= 0
         * @return the X location at the end of the rendered text
         */
        @SuppressWarnings("MagicConstant")
        public int drawText(Segment segment, int x, int y, Graphics graphics, TabExpander e, int startOffset) {
            graphics.setFont(graphics.getFont().deriveFont(getFontStyle()));
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int a = fontMetrics.getAscent();
            int h = a + fontMetrics.getDescent();
            int w = Utilities.getTabbedTextWidth(segment, fontMetrics, 0, e, startOffset);
            int rX = x - 1;
            int rY = y - a;
            int rW = w + 2;
            if ((getFontStyle() & 0x10) != 0) {
                graphics.setColor(Color.decode("#EEEEEE"));
                graphics.fillRect(rX, rY, rW, h);
            }
            graphics.setColor(getColor());
            x = Utilities.drawTabbedText(segment, x, y, graphics, e, startOffset);
            if ((getFontStyle() & 0x8) != 0) {
                graphics.setColor(Color.RED);
                graphics.drawRect(rX, rY, rW, h);
            }
            return x;
        }
    }
}
