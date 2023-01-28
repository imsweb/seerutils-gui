package com.imsweb.seerutilsgui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import com.imsweb.seerutilsgui.SeerGuiUtils;
import com.imsweb.seerutilsgui.SeerWindow;

public class GoToLineDialog extends JDialog implements ActionListener, SeerWindow {

    private final JTextComponent _comp;

    private final int _maxLineNum;

    private final JTextField _field;

    private String _previousValue;

    public GoToLineDialog(Window owner, JTextComponent comp, int maxLineNum) {
        this(owner, comp, maxLineNum, SeerGuiUtils.COLOR_APPLICATION_BACKGROUND);
    }

    public GoToLineDialog(Window owner, JTextComponent comp, int maxLineNum, Color backgroundColor) {
        super(owner);

        _comp = comp;
        _maxLineNum = maxLineNum;

        this.setTitle("Go to Line");
        this.setModal(true);
        this.setResizable(false);
        this.setName("conf-file-editor-go-to-line-dlg");
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performCancel();
            }
        });

        JPanel contentPnl = SeerGuiUtils.createPanel();
        contentPnl.setLayout(new BoxLayout(contentPnl, BoxLayout.Y_AXIS));
        contentPnl.setOpaque(true);
        contentPnl.setBackground(backgroundColor);
        contentPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(contentPnl, BorderLayout.CENTER);

        // CENTER - content
        JPanel centerPnl = SeerGuiUtils.createPanel();
        centerPnl.setOpaque(true);
        centerPnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JPanel labelPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        labelPnl.add(SeerGuiUtils.createLabel("Enter line number (1.." + maxLineNum + "):"));
        centerPnl.add(labelPnl, BorderLayout.NORTH);
        JPanel fieldPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        fieldPnl.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        _field = new JTextField(20);
        _field.setFont(SeerGuiUtils.adjustFontSize(_field.getFont()));
        _field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    performOk();
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    performCancel();
            }
        });
        fieldPnl.add(_field);
        centerPnl.add(fieldPnl, BorderLayout.CENTER);
        contentPnl.add(centerPnl, BorderLayout.CENTER);

        // SOUTH - controls
        JPanel southPnl = SeerGuiUtils.createPanel();
        southPnl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        JPanel buttonsPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JButton okBtn = SeerGuiUtils.createButton("OK", "ok", "OK", this);
        buttonsPnl.add(okBtn);
        buttonsPnl.add(Box.createHorizontalStrut(10));
        JButton cancelBtn = SeerGuiUtils.createButton("Cancel", "cancel", "Cancel", this);
        buttonsPnl.add(cancelBtn);
        southPnl.add(buttonsPnl, BorderLayout.CENTER);
        contentPnl.add(southPnl, BorderLayout.SOUTH);
        SeerGuiUtils.synchronizedComponentsWidth(okBtn, cancelBtn);
    }

    public void performCancel() {
        SeerGuiUtils.hideAndDestroy(this, null, false);
    }

    public void performOk() {
        if (_field.getText() != null && _field.getText().equals(_previousValue))
            return;
        _previousValue = _field.getText();

        int line = -1;
        if (_field.getText() != null && _field.getText().trim().matches("\\d+"))
            line = Integer.parseInt(_field.getText().trim());
        if (line < 1 || line > _maxLineNum)
            line = -1;

        if (line == -1) {
            JOptionPane.showMessageDialog(this, "Invalid line number.", "Error", JOptionPane.ERROR_MESSAGE);
            _field.requestFocusInWindow();
            _field.selectAll();
            return;
        }

        try {
            int start = SyntaxUtils.getSyntaxDocument(_comp).getStartOfLineFromLineNumber(line - 1);
            _comp.setCaretPosition(start);
            // this will be fixed when the project stops supporting Java 8...
            Rectangle rect = _comp.modelToView(start);
            _comp.scrollRectToVisible(rect);
        }
        catch (BadLocationException e1) {
            throw new RuntimeException("Unable to go to line", e1);
        }
        SeerGuiUtils.hideAndDestroy(this, null, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("ok".equals(e.getActionCommand()))
            performOk();
        else if ("cancel".equals(e.getActionCommand()))
            performCancel();
    }

    @Override
    public boolean handleShortcut(int key) {
        return false;
    }

    @Override
    public String getWindowId() {
        return getName();
    }
}
