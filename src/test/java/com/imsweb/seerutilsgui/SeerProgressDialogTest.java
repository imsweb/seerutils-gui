/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

public class SeerProgressDialogTest {

    public static void main(String[] args) {
        SeerGuiUtils.setupGuiEnvForSeerProject();
        JFrame myFrame = new JFrame("SeerProgressDialogTest");
        myFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 55));
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setPreferredSize(new Dimension(320, 200));

        myFrame.add(SeerGuiUtils.createButton("Use Spinner", null, null, e -> {
            SeerProgressDialog<Void, Void> dialog = new SeerProgressDialog<>(null, 10, "Spinning", "Counting");
            dialog.showSpinner();
            dialog.setSpinningLabel(JOptionPane.showInputDialog("Choose label"));
            SeerGuiUtils.showAndPosition(dialog, myFrame);
        }));
        myFrame.add(SeerGuiUtils.createButton("Use Progress Bar", null, null, e -> {
            SeerProgressDialog<Void, Void> dialog = new SeerProgressDialog<>(null, 10, "Spinning", "Counting");
            dialog.showProgress();
            dialog.setWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i <= 10; i++) {
                        if (dialog.isCanceled())
                            break;
                        dialog.setProgress(i);
                        dialog.setProgressBarLabel("Progress is: " + i);
                        Thread.sleep(500);
                    }
                    return null;
                }
            });
            SeerGuiUtils.showAndPosition(dialog, myFrame);
        }));
        myFrame.add(SeerGuiUtils.createButton("Test Cancel", null, null, e -> {
            SeerProgressDialog<Void, Void> dialog = new SeerProgressDialog<>(null, 100, "Spinning", "Counting");
            dialog.showProgress();
            dialog.setWorker(new SwingWorker<Void, Void>() {
                private int _count;

                @Override
                protected Void doInBackground() throws Exception {
                    for (_count = 0; _count <= 100; _count++) {
                        dialog.setProgress(_count);
                        dialog.setProgressBarLabel("Progress is: " + _count);
                        Thread.sleep(50);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (isCancelled())
                        JOptionPane.showMessageDialog(myFrame, "Progress stopped at " + _count);
                    else
                        JOptionPane.showMessageDialog(myFrame, "Progress completed!");

                    dialog.close();
                }
            });
            SeerGuiUtils.showAndPosition(dialog, myFrame);
        }));

        SeerGuiUtils.showAndPosition(myFrame, null);
    }
}


