/*
 * Copyright (C) 2010 Information Management Services, Inc.
 */
package com.imsweb.shared.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.painter.BusyPainter;

public class SeerSpinningPanel extends JPanel {

    private transient LockableUI _spinningUI;

    private JLabel _topLbl, _firstBottomLbl, _secondBottomLbl;

    public SeerSpinningPanel(int size, String topLbl, String bottomLbl1, String bottomLbl2) {
        this(size, topLbl, bottomLbl1, bottomLbl2, 20, 20, 10);
    }

    public SeerSpinningPanel(int size, String topLbl, String bottomLbl1, String bottomLbl2, int topGap, int bottomGap1, int bottomGap2) {

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        JPanel searchPnl = SeerGuiUtils.createPanel();
        searchPnl.setLayout(new BoxLayout(searchPnl, BoxLayout.Y_AXIS));

        if (topLbl != null) {
            JPanel searchTopPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchTopPnl.setOpaque(false);
            _topLbl = new JLabel(topLbl);
            searchTopPnl.add(_topLbl);
            searchPnl.add(searchTopPnl);
            searchPnl.add(Box.createVerticalStrut(topGap));
        }

        JPanel spinPnl = SeerGuiUtils.createPanel();
        spinPnl.setBorder(BorderFactory.createEmptyBorder());
        spinPnl.setPreferredSize(new Dimension(size, size));
        _spinningUI = new BusyPainterUI(size / 6, size / 3 * 2);
        JXLayer<JComponent> spinningPnl = new JXLayer<>(spinPnl, _spinningUI);
        spinningPnl.setOpaque(false);
        searchPnl.add(spinningPnl);

        if (bottomLbl1 != null) {
            searchPnl.add(Box.createVerticalStrut(bottomGap1));
            JPanel searchBottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchBottomPnl.setOpaque(false);
            _firstBottomLbl = new JLabel(bottomLbl1);
            searchBottomPnl.add(_firstBottomLbl);
            searchPnl.add(searchBottomPnl);
        }

        if (bottomLbl2 != null) {
            searchPnl.add(Box.createVerticalStrut(bottomGap2));
            JPanel searchBottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            searchBottomPnl.setOpaque(false);
            _secondBottomLbl = new JLabel(bottomLbl2);
            searchBottomPnl.add(_secondBottomLbl);
            searchPnl.add(searchBottomPnl);
        }

        JPanel searchWrapperPnl = new JPanel(new GridBagLayout());
        searchWrapperPnl.setOpaque(false);
        searchWrapperPnl.add(searchPnl);

        this.add(searchWrapperPnl, BorderLayout.CENTER);
    }

    public void startSpinning() {
        _spinningUI.setLocked(true);
    }

    public void stopSpinning() {
        _spinningUI.setLocked(false);
    }

    public void setTopLabel(String lbl) {
        if (_topLbl != null)
            _topLbl.setText(lbl);
    }

    public void setFirstBottomLabel(String lbl) {
        if (_firstBottomLbl != null)
            _firstBottomLbl.setText(lbl);
    }

    public void setSecondBottomLabel(String lbl) {
        if (_secondBottomLbl != null)
            _secondBottomLbl.setText(lbl);
    }

    private static class BusyPainterUI extends LockableUI implements ActionListener {

        private transient BusyPainter _busyPainter;

        private Timer _timer;

        private int _frameNumber = 0;

        public BusyPainterUI(int pointShape, int trajectory) {
            _busyPainter = new BusyPainter() {
                @Override
                protected void doPaint(Graphics2D g, Object object, int width, int height) {
                    Rectangle r = getTrajectory().getBounds();
                    int tw = width - r.width - 2 * r.x;
                    int th = height - r.height - 2 * r.y;
                    g.translate(tw / 2, th / 2);
                    super.doPaint(g, object, width, height);
                }
            };

            _busyPainter.setPointShape(new Ellipse2D.Double(0, 0, pointShape, pointShape));
            _busyPainter.setTrajectory(new Ellipse2D.Double(0, 0, trajectory, trajectory));

            _timer = new Timer(100, this);
        }

        @Override
        protected void paintLayer(Graphics2D g2, JXLayer<? extends JComponent> l) {
            super.paintLayer(g2, l);
            if (isLocked())
                _busyPainter.paint(g2, l, l.getWidth(), l.getHeight());
        }

        @Override
        public void setLocked(boolean isLocked) {
            super.setLocked(isLocked);
            if (isLocked)
                _timer.start();
            else
                _timer.stop();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            _frameNumber = (_frameNumber + 1) % 8;
            _busyPainter.setFrame(_frameNumber);
            setDirty(true);
        }
    }
}
