/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IconTest {
    public static void main(String[] args){
        IconTest myIcon = new IconTest();
        File dir1 = new File(".");
        System.out.println("current directory: " + dir1.getAbsolutePath());

        JFrame myFrame = new JFrame();
        JPanel myPanel = SeerGuiUtils.createContentPanel(myFrame);
        JLabel myLabel = new JLabel("image",new ImageIcon("src/test/java/com/imsweb/seerutilsgui/gui/icons/test1.png"),JLabel.CENTER);
        myPanel.add(myLabel);
        SeerGuiUtils.showAndPosition(myFrame, null);
        
        
    }
    public ImageIcon createIcon(String icon, String path){
        return new ImageIcon(path + icon);
    }

    
}
