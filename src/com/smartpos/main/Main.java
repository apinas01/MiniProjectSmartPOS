package com.smartpos.main;

import com.smartpos.ui.MainPOSFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainPOSFrame frame = new MainPOSFrame();
                frame.setVisible(true);
            }
        });
    }
}