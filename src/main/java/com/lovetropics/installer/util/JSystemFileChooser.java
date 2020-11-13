package com.lovetropics.installer.util;

import java.awt.Color;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class JSystemFileChooser extends JFileChooser {

    public void updateUI() {
        LookAndFeel old = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable ex) {
            old = null;
        }

        super.updateUI();

        if (old != null) {
            Color background = UIManager.getColor("Label.background");
            setBackground(background);
            setOpaque(true);
            try {
                UIManager.setLookAndFeel(old);
            } catch (UnsupportedLookAndFeelException ignored) {} // shouldn't get here
        }
    }
}
