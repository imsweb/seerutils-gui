/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

@SuppressWarnings("unused")
public class SeerHelpDialog extends JDialog implements SeerUniqueWindow {

    /**
     * Owner
     */
    private final Window _owner;

    /**
     * Whether the mouse is on the button
     */
    private boolean _mouseOnButton;

    /**
     * Background color (yellow)
     */
    private Color _backgroundColor = new Color(255, 249, 196);
    private Color _disclaimerBackgroundColor = new Color(255, 251, 220);

    /**
     * Main focus listener
     */
    private transient FocusListener _focusListener;

    /**
     * Global GUI components
     */
    private final JPanel _contentPnl;
    private JPanel _disclaimerPnl;
    private final JEditorPane _pane;
    private final JScrollPane _scrollPane;

    /**
     * Default constructor, used internally.
     * <p/>
     * Created on Oct 11, 2010 by depryf
     * @param owner owner window
     * @param dlgId unique dialog ID
     * @param title dialog title (will be shown only if the window gets decorated)
     * @param icon icon for the decorated frame
     */
    protected SeerHelpDialog(Window owner, String dlgId, String title, Image icon) {
        super(owner);

        this.setTitle(title);
        if (icon != null)
            this.setIconImage(icon);
        this.setName(dlgId);
        this.setFocusCycleRoot(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                performClose();
            }
        });

        _owner = owner;

        _contentPnl = SeerGuiUtils.createPanel();
        _pane = new JEditorPane();
        _pane.setEditorKit(new HTMLEditorKit() {
            @Override
            public Document createDefaultDocument() {
                // force a synchronous load (http://forums.sun.com/thread.jspa?threadID=700324&messageID=4066928)
                HTMLDocument doc = (HTMLDocument)(super.createDefaultDocument());
                doc.setAsynchronousLoadPriority(-1);

                StyleSheet styleSheet = getStyleSheet();

                // bullets look terrible in Swing, let's replace them with an image
                styleSheet.addRule("ul {list-style-image: url(bullet.png);}");

                // adjust the font if needed
                styleSheet.addRule("body { font-family: Serif; font-size: " + (14 + Math.floorDiv(SeerGuiUtils.getFontDelta(), 2)) + "pt; }");

                return doc;
            }
        });

        // let's set the base URL to the icon folder, that way images can very easily be added without any prefix
        ((HTMLDocument)_pane.getDocument()).setBase(Thread.currentThread().getContextClassLoader().getResource("icons/"));

        _pane.setEditable(false);
        _pane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        _pane.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performClose();
            }
        });
        _pane.setBackground(_backgroundColor);
        _pane.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        _scrollPane = new JScrollPane(_pane);
        _contentPnl.add(_scrollPane, BorderLayout.CENTER);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(_contentPnl);
        this.getContentPane().setBackground(_backgroundColor);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                _pane.setCaretPosition(0);
                _pane.requestFocusInWindow();
                SeerHelpDialog.this.removeComponentListener(this);
            }
        });
    }

    /**
     * This constructors receives the content of the page as a String; no hyperlinks can be then be used (use the one that takes an URL to use hyperlinks).
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param owner owner window
     * @param dlgId unique dialog ID
     * @param title dialog title (will be shown only if the window gets decorated)
     * @param icon icon for the decorated frame
     * @param content dialog content (HTML)
     * @param decorated if true, the window will be decorated
     */
    public SeerHelpDialog(Window owner, String dlgId, String title, Image icon, String content, boolean decorated) {
        this(owner, dlgId, title, icon, content, decorated, null);
    }

    /**
     * This constructor takes an extra dimension parameter; it it used only internally...
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param owner owner window
     * @param dlgId unique dialog ID
     * @param title dialog title (will be shown only if the window gets decorated)
     * @param icon icon for the decorated frame
     * @param content dialog content (HTML)
     * @param decorated if true, the window will be decorated
     * @param dim preferred dimension for the main entire dialog
     */
    protected SeerHelpDialog(Window owner, final String dlgId, final String title, final Image icon, final String content, boolean decorated, Dimension dim) {
        this(owner, dlgId, title, icon);

        _pane.addHyperlinkListener(e -> {
            if (e.getEventType() == EventType.ACTIVATED) {
                if (e.getURL() != null && e.getURL().getProtocol() != null && e.getURL().getProtocol().startsWith("http")) {
                    try {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE))
                            Desktop.getDesktop().browse(e.getURL().toURI());
                    }
                    catch (IOException | URISyntaxException ex) {
                        // ignore exception
                    }
                }
            }
        });

        // if the window is not decorated, do a few extra things...
        if (!decorated) {
            this.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
            this.setAlwaysOnTop(true);
            this.setUndecorated(true);

            // handle special mouse and keyboard events
            _pane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "focus");
            _pane.getActionMap().put("focus", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createFocusDialog(dlgId, title, icon, content);
                }
            });
            _pane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    createFocusDialog(dlgId, title, icon, content);
                }
            });
            _focusListener = new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    // nothing to do
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (!_mouseOnButton)
                        performClose();
                }
            };
            _pane.addFocusListener(_focusListener);

            // add a disclaimer panel at the bottom
            _disclaimerPnl = SeerGuiUtils.createPanel();
            _disclaimerPnl.setBackground(_disclaimerBackgroundColor);
            _disclaimerPnl.setOpaque(true);
            _disclaimerPnl.setBorder(new SeerOptionalSidesLineBorder(Color.GRAY, false, true, true, true));
            JLabel focusLabel = SeerGuiUtils.createLabel("Click the window to move or re-size it. Press Esc to close it.   ", Font.ITALIC, 10, Color.DARK_GRAY);
            focusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            focusLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    createFocusDialog(dlgId, title, icon, content);
                }
            });
            _disclaimerPnl.add(focusLabel, BorderLayout.SOUTH);
            _contentPnl.add(_disclaimerPnl, BorderLayout.SOUTH);
        }
        else
            _scrollPane.setBorder(null);
        _pane.setText(content);

        // if a dimension is provided, use it
        if (dim != null)
            this.getContentPane().setPreferredSize(dim);
    }

    /**
     * This constructors receives the content of the page as a URL; hyperlinks can be used.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param parent parent window
     * @param dlgId unique dialog ID
     * @param title dialog title (will be shown only if the window gets decorated)
     * @param icon icon icon for the decorated frame
     * @param url dialog content
     */
    public SeerHelpDialog(Window parent, String dlgId, String title, Image icon, URL url) {
        this(parent, dlgId, title, icon);

        // since we are using a URL, we can enable hyperlinks...
        _pane.addHyperlinkListener(e -> {
            if (e.getEventType() == EventType.ACTIVATED) {
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent)e;
                    HTMLDocument doc = (HTMLDocument)_pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                }
                else {
                    try {
                        _pane.setPage(e.getURL());
                    }
                    catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        });

        // set the content of the page
        try {
            _pane.setPage(url);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Helper method to switch from an undecorated window to a decorated one.
     * <p/>
     * Created on Oct 11, 2010 by depryf
     * @param dlgId dialog ID
     * @param title dialog title
     * @param icon icon to use
     * @param content content
     */
    protected void createFocusDialog(String dlgId, String title, Image icon, String content) {

        // first, remove the main focus listener, we want to control when the light window should disappear
        _pane.removeFocusListener(_focusListener);

        // then calculate where the window should be shown, we want the scrollPane to be exactly at the same position
        Dimension dim = new Dimension(this.getContentPane().getSize().width, this.getContentPane().getSize().height - _disclaimerPnl.getPreferredSize().height);
        final SeerHelpDialog dlg = new SeerHelpDialog(_owner, dlgId, title, icon, content, true, dim);
        dlg.pack();
        int borderSize = (dlg.getPreferredSize().width - this.getPreferredSize().width) / 2;
        int headerSize = dlg.getPreferredSize().height - this.getPreferredSize().height - borderSize;
        dlg.setLocation(new Point(this.getLocationOnScreen().x - borderSize, this.getLocationOnScreen().y - headerSize));

        // and finally show the new dialog and hide the old one...
        SwingUtilities.invokeLater(() -> {
            performClose();
            _pane.addFocusListener(_focusListener);
            SeerGuiUtils.show(dlg);
            dlg._scrollPane.getViewport().scrollRectToVisible(new Rectangle(_scrollPane.getViewport().getViewPosition()));
        });
    }

    /**
     * Sets the background color (default is a light yellow).
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param c1 background color
     * @param c2 disclaimer background color
     */
    public void setBackgroundColor(Color c1, Color c2) {
        _backgroundColor = c1;
        _pane.setBackground(c1);
        if (_disclaimerPnl != null) {
            _disclaimerBackgroundColor = c2;
            _disclaimerPnl.setBackground(c2);
        }
    }

    /**
     * Sets the HTML document base to the provided URL (useful to display images for example).
     * <p/>
     * Created on Oct 11, 2010 by depryf
     * @param base base document
     */
    public void setHtmlDocumentBase(URL base) {
        ((HTMLDocument)_pane.getDocument()).setBase(base);
    }

    /**
     * Performs the close action.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     */
    public void performClose() {
        if (this.isUndecorated()) {
            this.setVisible(false);
            this.dispose();
        }
        else
            SeerGuiUtils.hideAndDestroy(this);
    }

    public void setMouseOnButton(boolean mouseOnButton) {
        _mouseOnButton = mouseOnButton;
    }

    /**
     * Positions the dialog relative to the given component, never going outside the parent.
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param relativeTo relative to component
     */
    public void setRelativeComponent(JComponent relativeTo, Window parent) {
        int defaultFontDelta = SeerGuiUtils.getFontDelta();

        // first calculate preferred size based on content (note that this size can changed based on the relative component, see calculatePosition())
        this.pack();
        int w = Math.min(Math.min(980, parent.getWidth() - 75), Math.min(680, _pane.getPreferredScrollableViewportSize().width + 20 + defaultFontDelta));
        int h = Math.min(Math.min(980, parent.getHeight() - 75), Math.min(680, _pane.getPreferredScrollableViewportSize().height + 20 + defaultFontDelta));
        this.setPreferredSize(new Dimension(w, h));

        // then calculate the preferred location
        this.pack();
        this.setLocation(calculatePosition(relativeTo, parent));
    }

    /**
     * Returns the main editor pane.
     * @return the main editor pane, never null
     */
    public JEditorPane getEditorPane() {
        return _pane;
    }

    /**
     * Calculates the location of this dialog relative to the provided component (usually an icon button).
     * <p/>
     * Created on Aug 2, 2010 by depryf
     * @param relativeTo relative to component
     * @param parent parent window
     * @return the calculated position
     */
    protected Point calculatePosition(JComponent relativeTo, Window parent) {
        Point topLeft = relativeTo.getLocationOnScreen();
        int disclaimerHeight = this.isUndecorated() ? _disclaimerPnl.getPreferredSize().height : 0;

        // by default, try to show the window on the bottom right side of the relative component
        int x = topLeft.x + relativeTo.getWidth() + 1;
        int y = topLeft.y + relativeTo.getHeight() + 1;

        // make sure it is not getting out of bound horizontally
        if (x + this.getPreferredSize().getWidth() > parent.getLocationOnScreen().x + parent.getWidth()) {
            // put it on the side where we have the most space
            int leftSpace = relativeTo.getLocationOnScreen().x - parent.getLocationOnScreen().x;
            int rightSpace = (parent.getLocationOnScreen().x + parent.getWidth()) - (relativeTo.getLocationOnScreen().x + relativeTo.getPreferredSize().width);
            if (leftSpace > rightSpace) {
                this.setPreferredSize(new Dimension(Math.min(this.getPreferredSize().width, leftSpace - 35), this.getPreferredSize().height + disclaimerHeight));
                x = Math.max(parent.getLocationOnScreen().x, topLeft.x - this.getPreferredSize().width - 1);
            }
            else {
                this.setPreferredSize(new Dimension(Math.min(this.getPreferredSize().width, rightSpace - 35), this.getPreferredSize().height + disclaimerHeight));
                x = Math.min(parent.getLocationOnScreen().x + parent.getWidth() - this.getPreferredSize().width, topLeft.x + relativeTo.getWidth() + 1);
            }

            // the width changed, so let's recalculate the preferred height
            _pane.setSize(this.getPreferredSize().width, Integer.MAX_VALUE);
            this.setPreferredSize(new Dimension(this.getPreferredSize().width, _pane.getPreferredScrollableViewportSize().height + 4 + disclaimerHeight));
        }

        // make sure it is not getting out of bound vertically
        if (y + this.getPreferredSize().getHeight() > parent.getLocationOnScreen().y + parent.getHeight()) {
            // put it on the side where we have the most space
            int topSpace = relativeTo.getLocationOnScreen().y - parent.getLocationOnScreen().y;
            int bottomSpace = (parent.getLocationOnScreen().y + parent.getHeight()) - (relativeTo.getLocationOnScreen().y + relativeTo.getPreferredSize().height + disclaimerHeight);
            if (topSpace > bottomSpace) {
                this.setPreferredSize(new Dimension(this.getPreferredSize().width, Math.min(this.getPreferredSize().height, topSpace - 35)));
                y = Math.max(parent.getLocationOnScreen().y, topLeft.y - this.getPreferredSize().height - 1);
            }
            else {
                this.setPreferredSize(new Dimension(this.getPreferredSize().width, Math.min(this.getPreferredSize().height, bottomSpace - 35) + disclaimerHeight));
                y = Math.min(parent.getLocationOnScreen().y + parent.getHeight() - this.getPreferredSize().height, topLeft.y + relativeTo.getHeight() + 1);
            }
        }

        return new Point(x, y);
    }

    @Override
    public String getWindowId() {
        return getName();
    }

    @Override
    public boolean handleShortcut(int key) {
        boolean handled = false;

        if (key == KeyEvent.VK_ESCAPE) {
            performClose();
            handled = true;
        }

        return handled;
    }
}
