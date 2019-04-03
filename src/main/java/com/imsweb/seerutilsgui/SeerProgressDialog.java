/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class SeerProgressDialog<T, V> extends JDialog {

    // the card panel IDs
    private static final String _PROGRESS_PANEL_ID = "card-with-progress";
    private static final String _SPINNING_PANEL_ID = "card-with-spinner";

    // global GUI components
    private SeerCardPanel _progressOrSpinnerPnl;
    private SeerProgressPanel _progressPnl;
    private String _progressText;
    private SeerSpinningPanel _spinnerPnl;
    private String _spinnerText;
    private JButton _cancelBtn;

    private SwingWorker<T, V> _worker;

    // is the worker canceled?
    private boolean _canceled = false;

    public SeerProgressDialog(Window owner, int numberToProcess, String spinnerText, String progressText) {
        this(owner, numberToProcess, spinnerText, progressText, true);
    }

    public SeerProgressDialog(Window owner, int numberToProcess, String spinnerText, String progressText, boolean showCancel) {
        super(owner);

        this.setTitle("Progress");
        this.setName("progress-dlg");
        this.setModal(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                cancel();
            }
        });

        _spinnerText = spinnerText;
        _progressText = progressText;

        setLayout(new BorderLayout());

        //progress panel (can be both progress and spinner)
        _progressOrSpinnerPnl = new SeerCardPanel();
        _progressPnl = new SeerProgressPanel(0, numberToProcess, 100, _progressText, "", "", 20, 0, 0);
        JPanel progressPnl = SeerGuiUtils.createPanel();
        progressPnl.add(_progressPnl, BorderLayout.CENTER);
        _spinnerPnl = new SeerSpinningPanel(24, _spinnerText, "", "", 20, 0, 0);
        JPanel spinnerPnl = SeerGuiUtils.createPanel();
        spinnerPnl.add(_spinnerPnl, BorderLayout.CENTER);
        _progressOrSpinnerPnl.add(progressPnl, _PROGRESS_PANEL_ID);
        _progressOrSpinnerPnl.add(spinnerPnl, _SPINNING_PANEL_ID);
        _progressOrSpinnerPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        if (spinnerText != null)
            showSpinner();
        else
            showProgress();
        add(_progressOrSpinnerPnl, BorderLayout.CENTER);

        //cancel button
        if (showCancel) {
            JPanel cancelBtnPnl = SeerGuiUtils.createPanel(new FlowLayout(FlowLayout.CENTER));
            cancelBtnPnl.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            _cancelBtn = new JButton("Cancel");
            _cancelBtn.addActionListener(e -> cancel());
            cancelBtnPnl.add(_cancelBtn);
            add(cancelBtnPnl, BorderLayout.SOUTH);
        }

        // set focus on cancel button when window becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (_worker != null)
                    _worker.execute();
                if (_cancelBtn != null)
                    _cancelBtn.requestFocusInWindow();
                SeerProgressDialog.this.removeComponentListener(this);
            }
        });
    }

    public void setProgress(int n) {
        _progressPnl.setProgress(n);
    }

    public void showSpinner() {
        _spinnerPnl.startSpinning();
        _progressOrSpinnerPnl.showPage(_SPINNING_PANEL_ID);
    }

    public void showProgress() {
        _spinnerPnl.stopSpinning();
        _progressOrSpinnerPnl.showPage(_PROGRESS_PANEL_ID);
    }

    public void setWorker(SwingWorker<T, V> worker) {
        _worker = worker;
    }

    public void setProgressBarLabel(String text) {
        _progressText = text;
        _progressPnl.setTopLabel(_progressText);
    }

    public void setSpinningLabel(String text) {
        _spinnerText = text;
        _spinnerPnl.setTopLabel(_spinnerText);
    }

    public void cancel() {
        _canceled = true;
        if (_worker != null)
            _worker.cancel(true);
        SeerGuiUtils.hideAndDestroy(this);
    }

    public boolean isCanceled() {
        return _canceled;
    }

    public void close() {
        setVisible(false);
        SeerGuiUtils.hideAndDestroy(this);
    }

}
