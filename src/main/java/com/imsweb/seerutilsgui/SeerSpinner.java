package com.imsweb.seerutilsgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SeerSpinner extends JPanel implements ActionListener {

    private static final int _NUM_POINTS = 8;

    private static final Color _COLOR_BASE = Color.LIGHT_GRAY;

    private static final Color _COLOR_HIGHLIGHT = Color.BLACK;

    private int _currentFrame;

    private final transient Shape _pointShape;
    private final transient Shape _trajectoryShape;

    private final Timer _timer;

    public SeerSpinner(int size) {
        int point = size / 6;
        int trajectory = size / 3 * 2;

        _currentFrame = -1;
        _pointShape = new Ellipse2D.Double(0, 0, point, point);
        _trajectoryShape = new Ellipse2D.Double(0, 0, trajectory, trajectory);
        _timer = new Timer(100, this);

        setBorder(BorderFactory.createEmptyBorder());
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
    }

    private void doPaint(Graphics2D g, int width, int height) {
        Rectangle r = _trajectoryShape.getBounds();
        int tw = width - r.width - 2 * r.x;
        int th = height - r.height - 2 * r.y;

        g.translate(tw / 2, th / 2);

        PathIterator pi = _trajectoryShape.getPathIterator(null);
        float[] coords = new float[6];
        Point2D.Float cp = new Point2D.Float();
        Point2D.Float sp = new Point2D.Float();
        int ret;
        float totalDist = 0;
        List<float[]> segStack = new ArrayList<>();
        do {
            try {
                ret = pi.currentSegment(coords);
            }
            catch (NoSuchElementException e) {
                // invalid object definition - one of the bounds is zero or less
                return;
            }
            if (ret == PathIterator.SEG_LINETO || (ret == PathIterator.SEG_CLOSE && (sp.x != cp.x || sp.y != cp.y))) {
                //close by line
                float c = calcLine(coords, cp);
                totalDist += c;
                // move the point to the end (just so it is same for all of them
                segStack.add(new float[] {c, 0, 0, 0, 0, coords[0], coords[1], ret});
                cp.x = coords[0];
                cp.y = coords[1];
            }
            if (ret == PathIterator.SEG_MOVETO) {
                sp.x = cp.x = coords[0];
                sp.y = cp.y = coords[1];

            }
            if (ret == PathIterator.SEG_CUBICTO) {
                float c = calcCube(coords, cp);
                totalDist += c;
                segStack.add(new float[] {c, coords[0], coords[1], coords[2],
                        coords[3], coords[4], coords[5], ret});
                cp.x = coords[4];
                cp.y = coords[5];
            }
            if (ret == PathIterator.SEG_QUADTO) {
                float c = calcLengthOfQuad(coords, cp);
                totalDist += c;
                segStack.add(new float[] {c, coords[0], coords[1], 0, 0, coords[2],
                        coords[3], ret});
                cp.x = coords[2];
                cp.y = coords[3];
            }

            // got a starting point, center point on it.
            pi.next();
        } while (!pi.isDone());

        float nxtP = totalDist / _NUM_POINTS;
        List<Point2D.Float> pList = new ArrayList<>();
        pList.add(new Point2D.Float(sp.x, sp.y));
        int sgIdx = 0;
        float[] sgmt = segStack.get(sgIdx);
        float len = sgmt[0];
        float travDist = nxtP;
        Point2D.Float center = new Point2D.Float(sp.x, sp.y);
        for (int i = 1; i < _NUM_POINTS; i++) {
            while (len < nxtP) {
                sgIdx++;
                // Be carefull when messing around with points.
                sp.x = sgmt[5];
                sp.y = sgmt[6];
                sgmt = segStack.get(sgIdx);
                travDist = nxtP - len;
                len += sgmt[0];
            }
            len -= nxtP;
            Point2D.Float p = calcPoint(travDist, sp, sgmt, width, height);
            pList.add(p);
            center.x += p.x;
            center.y += p.y;
            travDist += nxtP;
        }

        // calculate center
        center.x = ((float)width) / 2;
        center.y = ((float)height) / 2;

        // draw the stuff
        int i = 0;
        g.translate(center.x, center.y);
        for (Point2D.Float p : pList)
            drawAt(g, i++, p, center);
        g.translate(-center.x, -center.y);

        g.translate(-tw / 2, -th / 2);
    }

    private void drawAt(Graphics2D g, int i, Point2D.Float p, Point2D.Float c) {
        g.setColor(calcFrameColor(i));
        paintRotatedCenteredShapeAtPoint(p, c, g);
    }

    @SuppressWarnings("java:S3358")
    private void paintRotatedCenteredShapeAtPoint(Point2D.Float p, Point2D.Float c, Graphics2D g) {
        Shape s = _pointShape;
        double hh = s.getBounds().getHeight() / 2;
        double wh = s.getBounds().getWidth() / 2;
        double t;
        double x;
        double y;
        double a = c.y - p.y;
        double b = p.x - c.x;
        double sa = Math.signum(a);
        double sb = Math.signum(b);
        sa = sa == 0 ? 1 : sa;
        sb = sb == 0 ? 1 : sb;
        a = Math.abs(a);
        b = Math.abs(b);
        t = Math.atan(a / b);
        t = sa > 0 ? sb > 0 ? -t : -Math.PI + t : sb > 0 ? t : Math.PI - t;
        x = Math.sqrt(a * a + b * b) - wh;
        y = -hh;
        g.rotate(t);
        g.translate(x, y);
        g.fill(s);
        g.translate(-x, -y);
        g.rotate(-t);
    }

    private Point2D.Float calcPoint(float dist2go, Point2D.Float startPoint, float[] sgmt, int w, int h) {
        Point2D.Float f = new Point2D.Float();

        if (sgmt[7] == PathIterator.SEG_LINETO) {
            // linear
            float a = sgmt[5] - startPoint.x;
            float b = sgmt[6] - startPoint.y;
            float pathLen = sgmt[0];
            f.x = startPoint.x + a * dist2go / pathLen;
            f.y = startPoint.y + b * dist2go / pathLen;
        }
        else if (sgmt[7] == PathIterator.SEG_QUADTO) {
            // quadratic curve
            Point2D.Float ctrl = new Point2D.Float(sgmt[1] / w, sgmt[2] / h);
            Point2D.Float end = new Point2D.Float(sgmt[5] / w, sgmt[6] / h);
            Point2D.Float start = new Point2D.Float(startPoint.x / w, startPoint.y / h);

            // trans coords from abs to rel
            f = getXY(dist2go / sgmt[0], start, ctrl, end);
            f.x *= w;
            f.y *= h;

        }
        else if (sgmt[7] == PathIterator.SEG_CUBICTO) {
            // bezier curve
            float x = Math.abs(startPoint.x - sgmt[5]);
            float y = Math.abs(startPoint.y - sgmt[6]);

            // trans coords from abs to rel
            float c1rx = Math.abs(startPoint.x - sgmt[1]) / x;
            float c1ry = Math.abs(startPoint.y - sgmt[2]) / y;
            float c2rx = Math.abs(startPoint.x - sgmt[3]) / x;
            float c2ry = Math.abs(startPoint.y - sgmt[4]) / y;
            f = getXY(dist2go / sgmt[0], c1rx, c1ry, c2rx, c2ry);

            float a = startPoint.x - sgmt[5];
            float b = startPoint.y - sgmt[6];

            f.x = startPoint.x - f.x * a;
            f.y = startPoint.y - f.y * b;
        }

        return f;
    }

    private float calcLine(float[] coords, Point2D.Float cp) {
        float a = cp.x - coords[0];
        float b = cp.y - coords[1];
        return (float)Math.sqrt(a * a + b * b);
    }

    private float calcCube(float[] coords, Point2D.Float cp) {
        float x = Math.abs(cp.x - coords[4]);
        float y = Math.abs(cp.y - coords[5]);

        // trans coords from abs to rel
        float c1rx = Math.abs(cp.x - coords[0]) / x;
        float c1ry = Math.abs(cp.y - coords[1]) / y;
        float c2rx = Math.abs(cp.x - coords[2]) / x;
        float c2ry = Math.abs(cp.y - coords[3]) / y;
        float prevLength = 0;
        float prevX = 0;
        float prevY = 0;
        for (float t = 0.01f; t <= 1.0f; t += .01f) {
            Point2D.Float xy = getXY(t, c1rx, c1ry, c2rx, c2ry);
            prevLength += (float)Math.sqrt((xy.x - prevX) * (xy.x - prevX) + (xy.y - prevY) * (xy.y - prevY));
            prevX = xy.x;
            prevY = xy.y;
        }

        // prev len is a fraction num of the real path length
        return ((Math.abs(x) + Math.abs(y)) / 2) * prevLength;
    }

    private float calcLengthOfQuad(float[] coords, Point2D.Float cp) {
        Point2D.Float ctrl = new Point2D.Float(coords[0], coords[1]);
        Point2D.Float end = new Point2D.Float(coords[2], coords[3]);

        // get abs values
        float c1ax = Math.abs(cp.x - ctrl.x);
        float c1ay = Math.abs(cp.y - ctrl.y);
        float e1ax = Math.abs(cp.x - end.x);
        float e1ay = Math.abs(cp.y - end.y);
        float maxX = Math.max(c1ax, e1ax);
        float maxY = Math.max(c1ay, e1ay);

        // trans coords from abs to rel
        ctrl.x = c1ax / maxX;
        ctrl.y = c1ay / maxY;
        end.x = e1ax / maxX;
        end.y = e1ay / maxY;

        // calculate length
        float prevLength = 0;
        float prevX = 0;
        float prevY = 0;
        for (float t = 0.01f; t <= 1.0f; t += .01f) {
            Point2D.Float xy = getXY(t, new Point2D.Float(0, 0), ctrl, end);
            prevLength += (float)Math.sqrt((xy.x - prevX) * (xy.x - prevX)
                    + (xy.y - prevY) * (xy.y - prevY));
            prevX = xy.x;
            prevY = xy.y;
        }

        // prev len is a fraction num of the real path length
        float a = Math.abs(coords[2] - cp.x);
        float b = Math.abs(coords[3] - cp.y);
        float dist = (float)Math.sqrt(a * a + b * b);

        return prevLength * dist;
    }

    private Point2D.Float getXY(float t, float x1, float y1, float x2, float y2) {
        float invT = (1 - t);
        float b1 = 3 * t * (invT * invT);
        float b2 = 3 * (t * t) * invT;
        float b3 = t * t * t;
        return new Point2D.Float((b1 * x1) + (b2 * x2) + b3, (b1 * y1) + (b2 * y2) + b3);
    }

    private Point2D.Float getXY(float t, Point2D.Float begin, Point2D.Float ctrl, Point2D.Float end) {
        float invT = (1 - t);
        float b0 = invT * invT;
        float b1 = 2 * t * invT;
        float b2 = t * t;
        return new Point2D.Float(b0 * begin.x + (b1 * ctrl.x) + b2 * end.x, b0 * begin.y + (b1 * ctrl.y) + b2 * end.y);
    }

    private Color calcFrameColor(final int i) {
        if (_currentFrame == -1)
            return _COLOR_BASE;

        for (int t = 0; t < 4; t++) {
            if (i == (_currentFrame - t + _NUM_POINTS) % _NUM_POINTS) {
                float terp = 1 - ((float)(4 - t)) / (float)4;
                return interpolate(_COLOR_BASE, _COLOR_HIGHLIGHT, terp);
            }
        }

        return _COLOR_BASE;
    }

    @SuppressWarnings("SameParameterValue")
    private Color interpolate(Color b, Color a, float t) {
        float[] acomp = a.getRGBComponents(null);
        float[] bcomp = b.getRGBComponents(null);
        float[] ccomp = new float[4];

        for (int i = 0; i < 4; i++)
            ccomp[i] = acomp[i] + (bcomp[i] - acomp[i]) * t;

        return new Color(ccomp[0], ccomp[1], ccomp[2], ccomp[3]);
    }

    public void startSpinning() {
        _timer.start();
    }

    public void stopSpinning() {
        _timer.stop();
    }

    @Override
    public void paint(Graphics g) {
        if (!(g instanceof Graphics2D))
            return;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        doPaint(g2d, this.getWidth(), this.getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _currentFrame = (_currentFrame + 1) % 8;
        this.invalidate();
        this.repaint();
    }
}
